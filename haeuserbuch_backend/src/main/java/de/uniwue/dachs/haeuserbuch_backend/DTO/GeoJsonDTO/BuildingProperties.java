package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<Source> primary_sources = new HashSet<>();
    private List<String> secondary_sources = new ArrayList<>();
    private String notes;
}
