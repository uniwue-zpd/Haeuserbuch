package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.FeatureCollection;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper.PlaceToDTO;
import static de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper.DtoToPlace;
import static de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper.PlaceToGeoJson;
import static de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.PlaceMapper.GeoJsonToPlace;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.*;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    // GET all places
    public List<PlaceDTO> getAllPlaces() {
        List<Place> places = placeRepository.findAll();
        List<PlaceDTO> placeDTOs = new ArrayList<>();
        places.forEach(place -> placeDTOs.add(PlaceToDTO(place)));
        return placeDTOs;
    }

    // GET all places as feature collection
    public FeatureCollection getAllPlaceFeatures() {
        List<Place> places = placeRepository.findAll();
        FeatureCollection featureCollection = new FeatureCollection();
        List<Feature> features = new ArrayList<>();
        places.forEach(place -> features.add(PlaceToGeoJson(place)));
        featureCollection.setFeatures(features);
        return featureCollection;
    }

    // GET a place by its ID
    public Optional<PlaceDTO> getPlaceById(Long id) {
        return placeRepository.findById(id).map(PlaceMapper::PlaceToDTO);
    }

    // GET a place by its ID (GeoJSON)
    public Optional<Feature> getPlaceFeatureById(Long id) {
        return placeRepository.findById(id).map(PlaceMapper::PlaceToGeoJson);
    }

    // POST Create new place
    @Transactional
    public void createPlace(PlaceDTO placeDTO) {
        Place place = DtoToPlace(placeDTO);
        placeRepository.save(place);
    }

    // POST Create new place from GeoJSON
    @Transactional
    public void createPlaceFromGeoJSON(Feature feature) {
        Place place = GeoJsonToPlace(feature);
        placeRepository.save(place);
    }

    // PUT Update existing place
    @Transactional
    public void updatePlace(Long id, PlaceDTO updatedPlaceDTO) {
        placeRepository.findById(id).map(entity -> {
            entity.setReal_name(updatedPlaceDTO.getReal_name());
            entity.setAlt_names(updatedPlaceDTO.getAlt_names());
            entity.setCoordinates((updatedPlaceDTO.getCoordinates() != null)
                    ? createPoint(updatedPlaceDTO.getCoordinates())
                    : null);
            entity.setNotes(updatedPlaceDTO.getNotes());
            return placeRepository.save(entity);
        }).orElseThrow(() -> new NoSuchElementException("Place with ID " + id + " does not exist"));
    }

    // PUT Update existing place from GeoJSON
    @Transactional
    public void updatePlaceFromGeoJSON(Long id, Feature updatedFeature) {
        Place updatedPlace = GeoJsonToPlace(updatedFeature);
        placeRepository.findById(id).map(entity -> {
            entity.setReal_name(updatedPlace.getReal_name());
            entity.setAlt_names(updatedPlace.getAlt_names());
            entity.setCoordinates(updatedPlace.getCoordinates());
            entity.setNotes(updatedPlace.getNotes());
            return placeRepository.save(entity);
        }).orElseThrow(() -> new NoSuchElementException("Place with ID " + id + " does not exist"));
    }

    // DELETE place by ID
    @Transactional
    public void deletePlace(Long id) {
        if (!placeRepository.existsById(id)) {
            throw new RuntimeException("Place with id '" + id + "' does not exist");
        }
        placeRepository.deleteById(id);
    }
}
