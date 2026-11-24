package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.CitizenshipPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterEntryRow;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterRow;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.FeatureCollection;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PlaceProperties;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PointGeometry;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PersonMapper;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.*;
import static de.uniwue.dachs.haeuserbuch_backend.utils.RegisterUtils.*;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;
    private final PersonMapper personMapper;
    private final CitizenshipService citizenshipService;

    public PlaceService(PlaceRepository placeRepository,
                        PlaceMapper placeMapper,
                        PersonMapper personMapper,
                        CitizenshipService citizenshipService) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
        this.personMapper = personMapper;
        this.citizenshipService = citizenshipService;
    }

    // GET all places as feature collection
    @Cacheable("places")
    public FeatureCollection getAllPlaces() {
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setFeatures(
                placeRepository.findAll().stream()
                        .map(placeMapper::PlaceToFeature)
                        .sorted(Comparator.comparing(Feature::getId))
                        .toList()
        );
        return featureCollection;
    }

    // GET a place by its ID
    @Cacheable(value = "places", key = "#id")
    public Optional<Feature> getPlaceById(Long id) {
        return placeRepository.findById(id).map(placeMapper::PlaceToFeature);
    }

    // POST Create new place
    @Transactional
    @CacheEvict(value = "places", allEntries = true)
    @SearchIndexAffecting
    public Feature createPlace(Feature feature) {
        return placeMapper.PlaceToFeature(placeRepository.save(placeMapper.FeatureToPlace(feature)));
    }

    // PUT Update existing place
    @Transactional
    @CacheEvict(value = "places", allEntries = true)
    @SearchIndexAffecting
    public Feature updatePlace(Long id, Feature updatedFeature) {
        return placeRepository.findById(id).map(entity -> {
            PlaceProperties properties = (PlaceProperties) updatedFeature.getProperties();
            entity.setRealName(properties != null ? properties.getRealName() : null);
            entity.setAltNames(properties != null ? properties.getAltNames() : null);
            entity.setIsUncertain(properties != null ? properties.getIsUncertain() : null);
            entity.setInternalNotes(properties != null ? properties.getInternalNotes() : null);
            entity.setGeneralNotes(properties != null ? properties.getGeneralNotes() : null);
            if (updatedFeature.getGeometry() != null) {
                if (updatedFeature.getGeometry() instanceof PointGeometry) {
                    List<Double> coordinates = ((PointGeometry) updatedFeature.getGeometry()).getCoordinates();
                    entity.setCoordinates(createPoint(coordinates));
                } else {
                    throw new IllegalArgumentException("Unsupported geometry type");
                }
            } else {
                entity.setCoordinates(null);
            }
            Place updatedPlace = placeRepository.save(entity);
            return placeMapper.PlaceToFeature(updatedPlace);
        }).orElseThrow(() -> new NoSuchElementException("Place with ID " + id + " does not exist"));
    }

    // DELETE place by ID
    @Transactional
    @CacheEvict(value = "places", allEntries = true)
    @SearchIndexAffecting
    public void deletePlace(Long id) {
        if (!placeRepository.existsById(id)) {
            throw new RuntimeException("Place with id '" + id + "' does not exist");
        }
        placeRepository.deleteById(id);
    }

    /**
     * Allows searching for places based on a search term.
     * @param query Search term.
     * @return A {@link List} of {@link PlaceDTO} matching the search term or an empty {@link List} if no places match the search term.
     */
    public List<PlaceDTO> searchPlaces(String query) {
        return placeRepository.searchPlaces(query).stream()
                .map(placeMapper::PlaceToDTO)
                .toList();
    }

    /**
     * Page of places that are the origin or emigration place of at least one person, each with its persons.
     * Places are sorted by name and filtered by real or alternative name.
     */
    @Transactional(readOnly = true)
    public Page<RegisterDTO<PersonPreviewDTO>> getPersonRegister(String name, Pageable pageable) {
        Page<RegisterRow> places = placeRepository.findPersonRegisterPlaces(normalizeName(name), unsorted(pageable));
        Map<Long, List<PersonPreviewDTO>> persons = personsByGroup(personRows(places), personMapper::RegisterEntryRowToPreviewDTO);
        return places.map(place -> new RegisterDTO<>(place.id(), place.name(), persons.getOrDefault(place.id(), List.of())));
    }

    /**
     * Page of places that are the origin or emigration place of a citizen, guarantor or mentioned person
     * of at least one citizenship, each with these citizenships.
     * Places are sorted by name and filtered by real or alternative name.
     */
    @Transactional(readOnly = true)
    public Page<RegisterDTO<CitizenshipPreviewDTO>> getCitizenshipRegister(String name, Pageable pageable) {
        Page<RegisterRow> places = placeRepository.findCitizenshipRegisterPlaces(normalizeName(name), unsorted(pageable));
        Map<Long, List<CitizenshipPreviewDTO>> citizenships =
                citizenshipService.getPreviewsByPersonGroups(personIdsByGroup(personRows(places)));
        return places.map(place -> new RegisterDTO<>(place.id(), place.name(), citizenships.getOrDefault(place.id(), List.of())));
    }

    /** Persons with one of the given places as origin or emigration place */
    private Stream<RegisterEntryRow> personRows(Page<RegisterRow> places) {
        List<Long> placeIds = places.map(RegisterRow::id).toList();
        if (placeIds.isEmpty()) return Stream.empty();
        return Stream.concat(
                placeRepository.findOriginPlaceEntries(placeIds).stream(),
                placeRepository.findEmigrationPlaceEntries(placeIds).stream()
        );
    }
}
