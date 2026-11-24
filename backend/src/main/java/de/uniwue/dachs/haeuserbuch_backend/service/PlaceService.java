package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.CitizenshipPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.CitizenshipPersonRow;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceEntryRow;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceRegisterDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceRegisterRow;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.repository.CitizenshipRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.FeatureCollection;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PlaceProperties;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PointGeometry;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.CitizenshipMapper;
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
    private final CitizenshipRepository citizenshipRepository;
    private final CitizenshipMapper citizenshipMapper;

    public PlaceService(PlaceRepository placeRepository,
                        PlaceMapper placeMapper,
                        PersonMapper personMapper,
                        CitizenshipRepository citizenshipRepository,
                        CitizenshipMapper citizenshipMapper) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
        this.personMapper = personMapper;
        this.citizenshipRepository = citizenshipRepository;
        this.citizenshipMapper = citizenshipMapper;
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
        Page<PlaceRegisterRow> places = placeRepository.findPersonRegisterPlaces(normalizeName(name), unsorted(pageable));

        // placeId -> personId -> person; a person can be origin and emigration of the same place
        Map<Long, Map<Long, PersonPreviewDTO>> personsByPlace = new HashMap<>();
        personRows(places).forEach(row -> personsByPlace
                .computeIfAbsent(row.placeId(), id -> new HashMap<>())
                .putIfAbsent(row.entryId(), personMapper.PlaceEntryRowToPreviewDTO(row)));

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

    /**
     * Page of places that are the origin or emigration place of a citizen, guarantor or mentioned person
     * of at least one citizenship, each with these citizenships.
     * Places are sorted by name and filtered by real or alternative name.
     */
    @Transactional(readOnly = true)
    public Page<PlaceRegisterDTO<CitizenshipPreviewDTO>> getCitizenshipRegister(String name, Pageable pageable) {
        Page<PlaceRegisterRow> places = placeRepository.findCitizenshipRegisterPlaces(normalizeName(name), unsorted(pageable));

        // placeId -> personIds of persons with this place as origin or emigration place
        Map<Long, Set<Long>> personIdsByPlace = new HashMap<>();
        personRows(places).forEach(row -> personIdsByPlace
                .computeIfAbsent(row.placeId(), id -> new HashSet<>())
                .add(row.entryId()));

        Set<Long> personIds = new HashSet<>();
        personIdsByPlace.values().forEach(personIds::addAll);

        // personId -> citizenships in which the person is citizen, guarantor or mentioned person
        Map<Long, List<CitizenshipPersonRow>> citizenshipsByPerson = new HashMap<>();
        if (!personIds.isEmpty()) {
            Stream.of(
                    citizenshipRepository.findByCitizenIds(personIds),
                    citizenshipRepository.findByGuarantorIds(personIds),
                    citizenshipRepository.findByMentionedPersonIds(personIds)
            ).flatMap(List::stream).forEach(row -> citizenshipsByPerson
                    .computeIfAbsent(row.personId(), id -> new ArrayList<>())
                    .add(row));
        }

        return places.map(place -> {
            // citizenshipId -> citizenship; several persons of a place can appear in the same citizenship
            Map<Long, CitizenshipPreviewDTO> citizenships = new HashMap<>();
            personIdsByPlace.getOrDefault(place.id(), Set.of()).stream()
                    .flatMap(personId -> citizenshipsByPerson.getOrDefault(personId, List.of()).stream())
                    .forEach(row -> citizenships.putIfAbsent(
                            row.citizenshipId(),
                            citizenshipMapper.CitizenshipPersonRowToPreviewDTO(row)));

            return new PlaceRegisterDTO<>(
                    place.id(),
                    place.realName(),
                    citizenships.values().stream()
                            .sorted(Comparator.comparing(
                                            CitizenshipPreviewDTO::getSignature,
                                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                                    .thenComparing(CitizenshipPreviewDTO::getId))
                            .toList()
            );
        });
    }

    private static String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    /** Register order is fixed in the query, so a client {@code sort} must not be appended */
    private static Pageable unsorted(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }

    /** Persons with one of the given places as origin or emigration place */
    private Stream<PlaceEntryRow> personRows(Page<PlaceRegisterRow> places) {
        List<Long> placeIds = places.map(PlaceRegisterRow::id).toList();
        if (placeIds.isEmpty()) return Stream.empty();
        return Stream.concat(
                placeRepository.findOriginPlaceEntries(placeIds).stream(),
                placeRepository.findEmigrationPlaceEntries(placeIds).stream()
        );
    }
}
