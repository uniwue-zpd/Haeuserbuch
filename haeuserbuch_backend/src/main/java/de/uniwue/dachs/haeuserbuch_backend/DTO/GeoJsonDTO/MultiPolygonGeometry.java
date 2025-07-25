package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MultiPolygonGeometry extends Geometry {
    private List<List<List<List<Double>>>> coordinates;
}
