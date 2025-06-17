package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.FeatureCollection;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.PointGeometry;
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
        places.forEach(place -> placeDTOs.add(PlaceToDTO(place)));
        return placeDTOs;
    }

    // Get all places as feature collection
    public FeatureCollection getAllPlaceFeatures() {
        List<Place> places = placeRepository.findAll();
        FeatureCollection featureCollection = new FeatureCollection();
        List<Feature> features = new ArrayList<>();
        places.forEach(place -> features.add(PlaceToGeoJson(place)));
        featureCollection.setFeatures(features);
        return featureCollection;
    }

    // Get a place by its ID
    public Optional<PlaceDTO> getPlaceById(Long id) {
        return placeRepository.findById(id).map(this::PlaceToDTO);
    }

    // Get a place by its ID (GeoJSON)
    public Optional<Feature> getPlaceFeatureById(Long id) {
        return placeRepository.findById(id).map(this::PlaceToGeoJson);
    }

    // Create new place
    public void createPlace(PlaceDTO placeDTO) {
        Place place = DtoToPlace(placeDTO);
        placeRepository.save(place);
    }

    // Create new place from GeoJSON
    public void createPlaceFromGeoJSON(Feature feature) {
        Place place = GeoJsonToPlace(feature);
        placeRepository.save(place);
    }

    // Helper methods
    private Place DtoToPlace(PlaceDTO placeDTO) {
        Place place = new Place();
        place.setReal_name(placeDTO.getReal_name());
        place.setAlt_names(placeDTO.getAlt_names());
        place.setCoordinates(createPoint(placeDTO.getCoordinates()));
        place.setNotes(placeDTO.getNotes());
        return place;
    }

    private Place GeoJsonToPlace(Feature feature) {
        if (!(feature.getGeometry() instanceof PointGeometry geometry)) {
            throw new IllegalArgumentException("Unsupported geometry type");
        }
        List<Double> coordinates = geometry.getCoordinates();
        if (coordinates.size() != 2) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        Place place = new Place();
        place.setReal_name((String) feature.getProperties().get("real_name"));
        Object obj = feature.getProperties().get("alt_names");
        if (obj instanceof List<?> list) {
            List<String> alt_names = list.stream().filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
            place.setAlt_names(alt_names);
        }
        place.setCoordinates(createPoint(coordinates));
        place.setNotes((String) feature.getProperties().get("notes"));
        return place;
    }

    private PlaceDTO PlaceToDTO(Place place) {
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setId(place.getId());
        placeDTO.setReal_name(place.getReal_name());
        placeDTO.setAlt_names(place.getAlt_names());
        placeDTO.setCoordinates(convertPoint(place.getCoordinates()));
        placeDTO.setNotes(place.getNotes());
        return placeDTO;
    }

    private Feature PlaceToGeoJson(Place place) {
        Feature feature = new Feature();
        feature.getProperties().put("id", place.getId());
        feature.getProperties().put("real_name", place.getReal_name());
        feature.getProperties().put("alt_names", place.getAlt_names());
        feature.getProperties().put("notes", place.getNotes());
        PointGeometry geometry = new PointGeometry();
        geometry.setCoordinates(convertPoint(place.getCoordinates()));
        feature.setGeometry(geometry);
        return feature;
    }
}
