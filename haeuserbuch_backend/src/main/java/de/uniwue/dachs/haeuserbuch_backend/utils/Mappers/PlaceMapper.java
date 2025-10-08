package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PlaceProperties;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PointGeometry;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.repository.PlaceRepository;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.*;

@Component
public class PlaceMapper {
    private final PlaceRepository placeRepository;

    public PlaceMapper(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public Place PlaceDTOToPlace(PlaceDTO placeDTO) {
        if (placeDTO == null) {
            return null;
        } if (placeDTO.getId() != null) {
            return placeRepository.findById(placeDTO.getId()).orElse(null);
        } else {
            Place place = new Place();
            place.setRealName(placeDTO.getRealName());
            place.setAltNames(placeDTO.getAltNames());
            placeRepository.save(place);
            return place;
        }
    }

    public PlaceDTO PlaceToDTO(Place place) {
        if (place == null) {
            return null;
        }
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setId(place.getId());
        placeDTO.setRealName(place.getRealName());
        return placeDTO;
    }

    public Place FeatureToPlace(Feature feature) {
        Place place = new Place();
        if (feature.getProperties() != null) {
            if (feature.getProperties() instanceof PlaceProperties properties) {
                place.setRealName(properties.getRealName());
                place.setAltNames(properties.getAltNames());
                place.setIsUncertain(properties.getIsUncertain());
                place.setInternalNotes(properties.getInternalNotes());
                place.setGeneralNotes(properties.getGeneralNotes());
            } else {
                throw new IllegalArgumentException("Unsupported properties type");
            }
        }
        if (feature.getGeometry() != null) {
            if (feature.getGeometry() instanceof PointGeometry) {
                List<Double> coordinates = ((PointGeometry) feature.getGeometry()).getCoordinates();
                place.setCoordinates(createPoint(coordinates));
            } else {
                throw new IllegalArgumentException("Unsupported geometry type");
            }
        }
        return place;
    }

    public Feature PlaceToFeature(Place place) {
        Feature feature = new Feature();
        PlaceProperties properties = new PlaceProperties();
        feature.setId(place.getId());
        properties.setRealName(place.getRealName());
        properties.setAltNames(place.getAltNames());
        properties.setIsUncertain(place.getIsUncertain());
        properties.setInternalNotes(place.getInternalNotes());
        properties.setGeneralNotes(place.getGeneralNotes());
        feature.setProperties(properties);
        if (place.getCoordinates() != null) {
            PointGeometry geometry = new PointGeometry();
            geometry.setCoordinates(convertPoint(place.getCoordinates()));
            feature.setGeometry(geometry);
        }
        return feature;
    }
}
