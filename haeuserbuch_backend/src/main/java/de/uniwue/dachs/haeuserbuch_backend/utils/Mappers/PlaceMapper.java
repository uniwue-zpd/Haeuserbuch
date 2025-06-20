package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Place;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.PointGeometry;

import java.util.List;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.convertPoint;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPoint;

public class PlaceMapper {
    public static Place DtoToPlace(PlaceDTO placeDTO) {
        Place place = new Place();
        place.setReal_name(placeDTO.getReal_name());
        place.setAlt_names(placeDTO.getAlt_names());
        place.setCoordinates(createPoint(placeDTO.getCoordinates()));
        place.setNotes(placeDTO.getNotes());
        return place;
    }

    public static Place GeoJsonToPlace(Feature feature) {
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

    public static PlaceDTO PlaceToDTO(Place place) {
        PlaceDTO placeDTO = new PlaceDTO();
        placeDTO.setId(place.getId());
        placeDTO.setReal_name(place.getReal_name());
        placeDTO.setAlt_names(place.getAlt_names());
        placeDTO.setCoordinates(convertPoint(place.getCoordinates()));
        placeDTO.setNotes(place.getNotes());
        return placeDTO;
    }

    public static Feature PlaceToGeoJson(Place place) {
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
