package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlaceProperties extends Properties {
    private String real_name;
    private List<String> alt_names = new ArrayList<>();
    private String notes;
}
