package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceRegisterDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceRegisterRow;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.*;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;
    private final PersonMapper personMapper;

    public PlaceService(PlaceRepository placeRepository,
                        PlaceMapper placeMapper,
                        PersonMapper personMapper) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
        this.personMapper = personMapper;
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
    public Page<PlaceRegisterDTO<PersonPreviewDTO>> getPersonRegister(String name, Pageable pageable) {
        Page<PlaceRegisterRow> places = placeRepository.findPersonRegisterPlaces(
                name == null ? "" : name.trim(),
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())
        );
        List<Long> placeIds = places.map(PlaceRegisterRow::id).toList();

        Map<Long, Map<Long, PersonPreviewDTO>> personsByPlace = new HashMap<>();
        if (!placeIds.isEmpty()) {
            Stream.concat(
                    placeRepository.findOriginPlaceEntries(placeIds).stream(),
                    placeRepository.findEmigrationPlaceEntries(placeIds).stream()
            ).forEach(row -> personsByPlace
                    .computeIfAbsent(row.placeId(), id -> new HashMap<>())
                    .putIfAbsent(row.entryId(), personMapper.PlaceEntryRowToPreviewDTO(row)));
        }

        return places.map(place -> new PlaceRegisterDTO<>(
                place.id(),
                place.realName(),
                personsByPlace.getOrDefault(place.id(), Map.of()).values().stream()
                        .sorted(Comparator.comparing(
                                PersonPreviewDTO::getFullName,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                        .toList()
        ));
    }
}
