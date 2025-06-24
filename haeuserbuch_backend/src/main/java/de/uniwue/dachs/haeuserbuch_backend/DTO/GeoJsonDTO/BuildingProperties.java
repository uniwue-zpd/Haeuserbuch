package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildingProperties extends Properties {
    private String name;
    private String house_number;
    private String part_type;
    private String special_status;
    private String quarter;
    private String district;
    private String district_house_number;
    private String source;
    private String note;
}
