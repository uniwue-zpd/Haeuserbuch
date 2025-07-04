package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.BuildingProperties;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PointGeometry;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PolygonGeometry;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.List;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.*;

public class BuildingMapper {
    public static Feature BuildingToFeature(Building building) {
        Feature feature = new Feature();
        BuildingProperties properties = new BuildingProperties();
        feature.setId(building.getId());
        properties.setName(building.getName());
        properties.setHouse_number(building.getHouse_number());
        properties.setPart_type(building.getPart_type());
        properties.setSpecial_status(building.getSpecial_status());
        properties.setQuarter(building.getQuarter());
        properties.setDistrict(building.getDistrict());
        properties.setDistrict_house_number(building.getDistrict_house_number());
        properties.setPrimary_source(building.getPrimary_source());
        properties.setSecondary_sources(building.getSecondary_sources());
        properties.setNotes(building.getNotes());
        feature.setProperties(properties);
        if (building.getCoordinates() != null) {
            Geometry geometry = building.getCoordinates();
            if (geometry instanceof Point) {
                PointGeometry pointGeometry = new PointGeometry();
                pointGeometry.setCoordinates(convertPoint(building.getCoordinates()));
                feature.setGeometry(pointGeometry);
            } else if (geometry instanceof Polygon) {
                PolygonGeometry polygonGeometry = new PolygonGeometry();
                List<List<List<Double>>> coordinates = convertPolygon(building.getCoordinates());
                polygonGeometry.setCoordinates(coordinates);
                feature.setGeometry(polygonGeometry);
            }
        }
        return feature;
    }

    public static Building FeatureToBuilding(Feature feature) {
        Building building = new Building();
        if (feature.getProperties() != null) {
            if (feature.getProperties() instanceof BuildingProperties properties) {
                building.setName(properties.getName());
                building.setHouse_number(properties.getHouse_number());
                building.setPart_type(properties.getPart_type());
                building.setSpecial_status(properties.getSpecial_status());
                building.setQuarter(properties.getQuarter());
                building.setDistrict(properties.getDistrict());
                building.setDistrict_house_number(properties.getDistrict_house_number());
                building.setPrimary_source(properties.getPrimary_source());
                building.setSecondary_sources(properties.getSecondary_sources());
                building.setNotes(properties.getNotes());
            } else {
                throw new IllegalArgumentException("Unsupported properties type");
            }
        }
        if (feature.getGeometry() != null) {
            if (feature.getGeometry() instanceof PointGeometry) {
                List<Double> coordinates = ((PointGeometry) feature.getGeometry()).getCoordinates();
                building.setCoordinates(createPoint(coordinates));
            } else if (feature.getGeometry() instanceof PolygonGeometry) {
                List<List<List<Double>>> coordinates = ((PolygonGeometry) feature.getGeometry()).getCoordinates();
                building.setCoordinates(createPolygon(coordinates));
            } else {
                throw new IllegalArgumentException("Unsupported geometry type");
            }
        }
        return building;
    }
}
