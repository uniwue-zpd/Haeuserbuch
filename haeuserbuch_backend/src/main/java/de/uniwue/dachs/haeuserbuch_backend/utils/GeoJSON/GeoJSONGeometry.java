package de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeoJSONGeometry {
    private GeometryTypes type;
    private List<Double[][]> coordinates;

    public enum GeometryTypes {
        Point, LineString, Polygon
    }
}
