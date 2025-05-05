package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.FeatureCollection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPoint;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.convertPoint;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    // Get all places
    public List<PlaceDTO> getAllPlaces() {
        List<Place> places = placeRepository.findAll();
        List<PlaceDTO> placeDTOs = new ArrayList<>();
        for (Place place : places) {
            PlaceDTO currentDTO = new PlaceDTO();
            currentDTO.setId(place.getId());
            currentDTO.setReal_name(place.getReal_name());
            currentDTO.setAlt_names(place.getAlt_names());
            currentDTO.setCoordinates(convertPoint(place.getCoordinates()));
            placeDTOs.add(currentDTO);
        }
        return placeDTOs;
    }

    // Get all places as feature collection
    public FeatureCollection getAllPlaceFeatures() {
        List<Place> places = placeRepository.findAll();
        FeatureCollection featureCollection = new FeatureCollection();
        List<Feature> features = new ArrayList<>();
        for (Place place : places) {
            Feature feature = new Feature();

            feature.getProperties().put("id", place.getId());
            feature.getProperties().put("real_name", place.getReal_name());
            feature.getProperties().put("alt_names", place.getAlt_names());

            feature.getGeometry().put("coordinates", convertPoint(place.getCoordinates()));
            feature.getGeometry().put("type", "Point");

            features.add(feature);
        }
        featureCollection.setFeatures(features);
        return featureCollection;
    }

    // Get a place by its ID
    public Optional<PlaceDTO> getPlaceById(Long id) {
        return placeRepository.findById(id).map(entity -> {
            PlaceDTO placeDTO = new PlaceDTO();
            placeDTO.setId(entity.getId());
            placeDTO.setReal_name(entity.getReal_name());
            placeDTO.setAlt_names(entity.getAlt_names());
            placeDTO.setCoordinates(convertPoint(entity.getCoordinates()));
            return placeDTO;
        });
    }

    // Get a place by its ID (geoJSON)
    public Optional<Feature> getPlaceFeatureById(Long id) {
        return placeRepository.findById(id).map(entity_feature -> {
            Feature feature = new Feature();

            feature.getProperties().put("id", entity_feature.getId());
            feature.getProperties().put("real_name", entity_feature.getReal_name());
            feature.getProperties().put("alt_names", entity_feature.getAlt_names());

            feature.getGeometry().put("type", "Point");
            List<Double> coordinates = convertPoint(entity_feature.getCoordinates());
            feature.getGeometry().put("coordinates", coordinates);
            return feature;
        });
    }

    // Create new place
    public void createPlace(PlaceDTO placeDTO) {
        Place place = new Place();
        place.setReal_name(placeDTO.getReal_name());
        place.setAlt_names(placeDTO.getAlt_names());
        place.setCoordinates(createPoint(placeDTO.getCoordinates()));
        placeRepository.save(place);
    }

    // Create new place from geoJSON
    public void createPlaceFromGeoJSON(Feature geoJSONFeature) {
        Place place = new Place();
        place.setReal_name((String) geoJSONFeature.getProperties().get("real_name"));
        place.setAlt_names((List<String>) geoJSONFeature.getProperties().get("alt_names"));
        List<Double> coordinates = (List<Double>) geoJSONFeature.getProperties().get("coordinates");
        place.setCoordinates(createPoint(coordinates));
        placeRepository.save(place);
    }
}
