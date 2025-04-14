package de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeoJSONFeature {
    private String type = "Feature";
    private GeoJSONProperties properties;
    private GeoJSONGeometry geometry;
}
