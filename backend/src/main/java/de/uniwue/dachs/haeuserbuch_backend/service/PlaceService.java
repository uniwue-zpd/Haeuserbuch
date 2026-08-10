package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.FeatureCollection;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PlaceProperties;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PointGeometry;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.*;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final PlaceMapper placeMapper;

    public PlaceService(PlaceRepository placeRepository,
                        PlaceMapper placeMapper) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
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
    public Feature createPlace(Feature feature) {
        return placeMapper.PlaceToFeature(placeRepository.save(placeMapper.FeatureToPlace(feature)));
    }

    // PUT Update existing place
    @Transactional
    @CacheEvict(value = "places", allEntries = true)
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
}
