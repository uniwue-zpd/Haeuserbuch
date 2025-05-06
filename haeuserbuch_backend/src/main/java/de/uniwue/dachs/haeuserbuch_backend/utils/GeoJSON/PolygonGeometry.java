package de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PolygonGeometry extends Geometry {
    private List<List<List<Double>>> coordinates;
}
