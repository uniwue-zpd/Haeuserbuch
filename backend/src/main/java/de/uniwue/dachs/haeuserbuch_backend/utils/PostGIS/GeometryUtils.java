package de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS;

import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.List;

public final class GeometryUtils {
    private static final int COORDINATE_DIMENSION = 2;
    private static final int SRID = 4326;

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), SRID);

    private GeometryUtils() {
        throw new AssertionError("Utility class");
    }

    /**
     * Converts a Point into a JTS {@link Point} instance
     * @param coordinates A {@link List} containing exactly two {@link Double} values representing the x and y coordinates of the point.
     * @return A {@link Point} instance created from the provided coordinates.
     */
    public static Point createPoint(List<Double> coordinates) {
        if (coordinates == null || coordinates.size() != COORDINATE_DIMENSION) {
            throw new IllegalArgumentException("Point must contain exactly two coordinates");
        }
        Coordinate coordinate = new Coordinate(coordinates.get(0), coordinates.get(1));
        return GEOMETRY_FACTORY.createPoint(coordinate);
    }

    /**
     * Converts a JTS {@link Point} instance into a GeoJSON-compatible coordinate list.
     * @param geometry A {@link Geometry} instance that is expected to be of type {@link Point}.
     * @return A {@link List} containing exactly two {@link Double} values representing the x and y coordinates of the point.
     * @throws IllegalArgumentException if the provided geometry is null or not of type {@link Point}.
     */
    public static List<Double> convertPoint(Geometry geometry) {
        Point point = requireGeometry(geometry, Point.class);
        return List.of(point.getX(), point.getY());
    }

    /**
     * Creates a JTS LineString from a list of GeoJSON coordinates.
     * @param lineStringCoordinates A {@link List} of {@link List} of {@link Double} values, where each inner list represents a point with exactly two coordinates (x and y).
     * @return A {@link LineString} instance created from the provided coordinates.
     */
    public static LineString createLineString(List<List<Double>> lineStringCoordinates) {
        requireNonEmpty(lineStringCoordinates, "LineString");
        return GEOMETRY_FACTORY.createLineString(toCoordinates(lineStringCoordinates));
    }

    /**
     * Creates a LineString from a JTS LineString
     * @param geometry {@link Geometry} instance that is expected to be of type {@link LineString}.
     * @return a LineString as {@link List} of {@link List} of {@link Double} values, where each inner list represents a point with exactly two coordinates (x and y).
      * @throws IllegalArgumentException if the provided geometry is null or not of type {@link LineString}.
     */
    public static List<List<Double>> convertLineString(Geometry geometry) {
        LineString lineString = requireGeometry(geometry, LineString.class);
        return convertCoordinates(lineString.getCoordinates());
    }

    /**
     * Converts a Polygon into its JTS representation
     * @param polygonCoordinates A Polygon
     * @return {@link Polygon} instance created from the provided coordinates
     * @throws IllegalArgumentException if the provided list of coordinates is null or empty.
     */
    public static Polygon createPolygon(List<List<List<Double>>> polygonCoordinates) {
        requireNonEmpty(polygonCoordinates, "Polygon");
        Coordinate[] outerCoordinates = toCoordinates(polygonCoordinates.getFirst());
        LinearRing outerRing = GEOMETRY_FACTORY.createLinearRing(outerCoordinates);
        LinearRing[] holes = new LinearRing[Math.max(0, polygonCoordinates.size() - 1)];
        for (int i = 1; i < polygonCoordinates.size(); i++) {
            holes[i - 1] = GEOMETRY_FACTORY.createLinearRing(toCoordinates(polygonCoordinates.get(i)));
        }
        return GEOMETRY_FACTORY.createPolygon(outerRing, holes);
    }

    /**
     * Converts a JTS Polygon instance in its geoJSON representation
     * @param geometry {@link Geometry} instance that is expected to be of type {@link Polygon}.
     * @return A {@link List} of {@link List} of {@link Double} values, where the first inner list represents the exterior ring and any subsequent inner lists represent interior rings (holes) of the polygon.
     * @throws IllegalArgumentException if the provided geometry is null or not of type {@link Polygon}.
     */
    public static List<List<List<Double>>> convertPolygon(Geometry geometry) {
        Polygon polygon = requireGeometry(geometry, Polygon.class);
        List<List<List<Double>>> coordinates = new ArrayList<>();
        coordinates.add(convertCoordinates(polygon.getExteriorRing().getCoordinates()));
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            coordinates.add(convertCoordinates(polygon.getInteriorRingN(i).getCoordinates()));
        }
        return coordinates;
    }

    /**
     * Creates a JTS MultiPolygon instance from its geoJSON representation
     * @param multiPolygonCoordinates geoJSON Multipolygon
     * @return a {@link MultiPolygon} instance created from the provided coordinates
     * @throws IllegalArgumentException if the provided list of coordinates is null or empty.
     */
    public static MultiPolygon createMultiPolygon(List<List<List<List<Double>>>> multiPolygonCoordinates) {
        requireNonEmpty(multiPolygonCoordinates, "MultiPolygon");
        Polygon[] polygons = multiPolygonCoordinates.stream()
                .map(GeometryUtils::createPolygon)
                .toArray(Polygon[]::new);
        return GEOMETRY_FACTORY.createMultiPolygon(polygons);
    }

    /**
     * Converts a JTS Multipolygon into its geoJSON representation
     * @param geometry {@link Geometry} instance that is expected to be of type {@link MultiPolygon}.
     * @return a geoJSON Multipolygon instance
     * @throws IllegalArgumentException if the provided geometry is null or not of type {@link MultiPolygon}.
     */
    public static List<List<List<List<Double>>>> convertMultiPolygon(Geometry geometry) {
        MultiPolygon multiPolygon = requireGeometry(geometry, MultiPolygon.class);
        List<List<List<List<Double>>>> coordinates = new ArrayList<>();
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            coordinates.add(convertPolygon(multiPolygon.getGeometryN(i)));
        }
        return coordinates;
    }

    /**
     * Converts a list of coordinate pairs into a coordinate array suitable for JTS geometry creation.
     * @param points {@link List} of {@link List} of {@link Double} values, where each inner list represents a point with exactly two coordinates (x and y).
     * @return {@link Coordinate} array created from the provided list of points.
     * @throws IllegalArgumentException if any point in the provided list is null or does not
     */
    private static Coordinate[] toCoordinates(List<List<Double>> points) {
        Coordinate[] coordinates = new Coordinate[points.size()];
        for (int i = 0; i < points.size(); i++) {
            List<Double> point = points.get(i);
            if (point == null || point.size() != COORDINATE_DIMENSION) {
                throw new IllegalArgumentException("Each point must contain exactly two coordinates");
            }
            coordinates[i] = new Coordinate(point.get(0), point.get(1));
        }
        return coordinates;
    }

    /**
     * Converts a JTS Coordinate array instance into a List of points
     * @param coordinates {@link Coordinate} array to be converted
     * @return A {@link List} of {@link List} of {@link Double} values, where each inner list represents a point with exactly two coordinates (x and y).
     * @throws IllegalArgumentException if the provided coordinate array is null.
     */
    private static List<List<Double>> convertCoordinates(Coordinate[] coordinates) {
        List<List<Double>> result = new ArrayList<>(coordinates.length);
        for (Coordinate coordinate : coordinates) {
            List<Double> point = List.of(coordinate.getX(), coordinate.getY());
            result.add(point);
        }
        return result;
    }

    /**
     * Checks if a list is null or empty and throws an IllegalArgumentException if it is.
     * @param list A list instance to be checked
     * @param geometryType A String value to be checked
     * @param <T> The type of the list elements
     * @throws IllegalArgumentException if the provided list is null or empty.
     */
    private static <T> void requireNonEmpty(List<T> list, String geometryType) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException(geometryType + " cannot be null or empty");
        }
    }

    /**
     * Checks whether the given geometry is non-null and of the expected type, throwing an IllegalArgumentException if either condition is not met.
     * @param geometry {@link Geometry} instance to be checked
     * @param expectedType class that the geometry is expected to be an instance of
     * @return the provided geometry cast to the expected type if it is valid
     * @param <T> the expected type of the geometry
     * @throws IllegalArgumentException if the provided geometry is null or not of the expected type.
     */
    private static <T extends Geometry> T requireGeometry(Geometry geometry, Class<T> expectedType) {
        if (geometry == null) throw new IllegalArgumentException("Geometry cannot be null");
        if (!expectedType.isInstance(geometry)) {
            throw new IllegalArgumentException("Expected "
                    + expectedType.getSimpleName()
                    + " but got "
                    + geometry.getGeometryType()
            );
        }
        return expectedType.cast(geometry);
    }
}
