package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PlaceProperties;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PointGeometry;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import org.springframework.stereotype.Component;

import java.util.List;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.*;

@Component
public class PlaceMapper {
    public Place FeatureToPlace(Feature feature) {
        Place place = new Place();
        if (feature.getProperties() != null) {
            if (feature.getProperties() instanceof PlaceProperties properties) {
                place.setReal_name(properties.getReal_name());
                place.setAlt_names(properties.getAlt_names());
                place.setNotes(properties.getNotes());
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
        properties.setReal_name(place.getReal_name());
        properties.setAlt_names(place.getAlt_names());
        properties.setNotes(place.getNotes());
        feature.setProperties(properties);
        if (place.getCoordinates() != null) {
            PointGeometry geometry = new PointGeometry();
            geometry.setCoordinates(convertPoint(place.getCoordinates()));
            feature.setGeometry(geometry);
        }
        return feature;
    }
}
