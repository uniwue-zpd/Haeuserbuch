package de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS;

import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;

public class GeometryUtils {
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    // Converts the coordinates to a point
    public static Point createPoint(List<Double> coordinates) {
        if (coordinates == null || coordinates.size() != 2) {
            return null;
        }
        Coordinate point = new Coordinate(coordinates.get(0), coordinates.get(1));
        return geometryFactory.createPoint(point);
    }

    // Converts the point to coordinates list
    public static List<Double> convertPoint(Point point) {
        if (point == null) {
            return null;
        }
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(point.getX());
        coordinates.add(point.getY());
        return coordinates;
    }

    public static Polygon createPolygon(List<List<Double>> polygon) {
        Coordinate[] coordinates = new Coordinate[polygon.size()];
        for (int i = 0; i < polygon.size(); i++) {
            List<Double> point = polygon.get(i);
            coordinates[i] = new Coordinate(point.get(0), point.get(1));
        }
        LinearRing ring = geometryFactory.createLinearRing(coordinates);
        return geometryFactory.createPolygon(ring, null);
    }

    // Converts the polygon to a shape
    public static List<List<Double>> convertPolygon(Polygon polygon) {
        Coordinate[] coordinates = polygon.getCoordinates();
        List<List<Double>> polygon_list = new ArrayList<>();
        for (Coordinate coordinate : coordinates) {
            List<Double> point = new ArrayList<>();
            point.add(coordinate.getX());
            point.add(coordinate.getY());
            polygon_list.add(point);
        }
        return polygon_list;
    }
}
