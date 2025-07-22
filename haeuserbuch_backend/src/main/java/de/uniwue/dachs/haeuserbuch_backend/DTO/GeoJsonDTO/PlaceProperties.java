package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlaceProperties extends Properties {
    private String realName;
    private List<String> altNames = new ArrayList<>();
    private String notes;
}
