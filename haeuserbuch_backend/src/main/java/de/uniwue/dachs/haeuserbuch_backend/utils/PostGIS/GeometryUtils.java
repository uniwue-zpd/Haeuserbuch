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
        List<Double> coordinates = new ArrayList<Double>();
        coordinates.add(point.getX());
        coordinates.add(point.getY());
        return coordinates;
    }

    // Converts the shape to a polygon
    public static Polygon createPolygon(Double[][] polygon) {
        Coordinate[] coordinates = new Coordinate[polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            coordinates[i] = new Coordinate(polygon[i][0], polygon[i][1]);
        }
        LinearRing ring = geometryFactory.createLinearRing(coordinates);
        return geometryFactory.createPolygon(ring, null);
    }

    // Converts the polygon to a shape
    public static Double[][] convertPolygon(Polygon polygon) {
        Coordinate[] coordinates = polygon.getCoordinates();
        Double[][] polygon_array = new Double[coordinates.length][2];
        for (int i = 0; i < coordinates.length; i++) {
            polygon_array[i][0] = coordinates[i].getX();
            polygon_array[i][1] = coordinates[i].getY();
        }
        return polygon_array;
    }
}
