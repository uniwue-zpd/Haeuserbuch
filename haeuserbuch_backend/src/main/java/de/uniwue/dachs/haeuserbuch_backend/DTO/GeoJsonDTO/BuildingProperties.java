package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private String primary_source;
    private List<String> secondary_sources = new ArrayList<>();
    private String notes;
}
