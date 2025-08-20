package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.FeatureCollection;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PlaceProperties;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PointGeometry;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
        List<Place> places = placeRepository.findAll();
        FeatureCollection featureCollection = new FeatureCollection();
        List<Feature> features = new ArrayList<>();
        places.forEach(place -> features.add(placeMapper.PlaceToFeature(place)));
        featureCollection.setFeatures(features);
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
    public void createPlace(Feature feature) {
        Place place = placeMapper.FeatureToPlace(feature);
        placeRepository.save(place);
    }

    // PUT Update existing place
    @Transactional
    @CachePut(value = "places", key = "#id")
    public void updatePlace(Long id, Feature updatedFeature) {
        placeRepository.findById(id).map(entity -> {
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
            }
            return placeRepository.save(entity);
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
}
