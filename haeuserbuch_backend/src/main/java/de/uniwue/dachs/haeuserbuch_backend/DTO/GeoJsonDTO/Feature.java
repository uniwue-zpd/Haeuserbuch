package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feature {
    private String type = "Feature";
    private Long id;
    private Properties properties;
    private Geometry geometry;
}
