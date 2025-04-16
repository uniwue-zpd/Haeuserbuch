package de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GeoJSONFeature {
    private String type = "Feature";
    private Map<String, Object> properties = new HashMap<>();
    private GeoJSONGeometry geometry;
}
