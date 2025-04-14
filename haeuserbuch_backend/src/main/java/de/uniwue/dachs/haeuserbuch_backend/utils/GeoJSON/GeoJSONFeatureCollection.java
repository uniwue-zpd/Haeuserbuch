package de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeoJSONFeatureCollection {
    private String type = "FeatureCollection";
    private List<GeoJSONFeature> features;
}
