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
    public static List<Double> convertPoint(Geometry geometry) {
        if (!(geometry instanceof Point point)) {
            throw new IllegalArgumentException("Geometry must be a Point");
        }
        List<Double> coordinates = new ArrayList<>();
        coordinates.add(point.getX());
        coordinates.add(point.getY());
        return coordinates;
    }

    public static List<List<List<Double>>> convertPolygon(Geometry geometry) {
        if (!(geometry instanceof Polygon polygon)) {
            throw new IllegalArgumentException("Geometry must be a Polygon");
        }
        List<List<List<Double>>> coordinates = new ArrayList<>();
        List<List<Double>> outer_coordinates = convertCoordinates(polygon.getExteriorRing().getCoordinates());
        coordinates.add(outer_coordinates);
        if (polygon.getNumInteriorRing() > 0) {
            for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
                coordinates.add(convertCoordinates(polygon.getInteriorRingN(i).getCoordinates()));
            }
        }
        return coordinates;
    }

    public static Polygon createPolygon(List<List<List<Double>>> polygon) {
        if (polygon == null || polygon.isEmpty()) {
            throw new IllegalArgumentException("Polygon ca not be null or empty");
        }
        Coordinate[] outer_coordinates = toCoordinates(polygon.getFirst());
        LinearRing outer_ring = geometryFactory.createLinearRing(outer_coordinates);
        if (polygon.size() == 1) {
            return geometryFactory.createPolygon(outer_ring, null);
        } else {
            LinearRing[] holes = new LinearRing[polygon.size() - 1];
            for (int i = 1; i < polygon.size(); i++) {
                Coordinate[] inner_coordinates = toCoordinates(polygon.get(i));
                holes[i - 1] = geometryFactory.createLinearRing(inner_coordinates);
            }
            return geometryFactory.createPolygon(outer_ring, holes);
        }
    }

    private static Coordinate[] toCoordinates(List<List<Double>> points) {
        Coordinate[] coordinates = new Coordinate[points.size()];
        for (int i = 0; i < points.size(); i++) {
            List<Double> point = points.get(i);
            if (point.size() != 2) {
                throw new IllegalArgumentException("Each point must have exactly two coordinates.");
            }
            coordinates[i] = new Coordinate(point.get(0), point.get(1));
        }
        return coordinates;
    }

    private static List<List<Double>> convertCoordinates(Coordinate[] coordinates) {
        List<List<Double>> ring = new ArrayList<>();
        for (Coordinate coordinate : coordinates) {
            List<Double> point = new ArrayList<>();
            point.add(coordinate.getX());
            point.add(coordinate.getY());
            ring.add(point);
        }
        return ring;
    }
}
