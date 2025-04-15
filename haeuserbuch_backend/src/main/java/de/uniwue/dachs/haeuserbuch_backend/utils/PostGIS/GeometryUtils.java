package de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

public class GeometryUtils {
    private static final GeometryFactory geometryFactory = new GeometryFactory();

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
