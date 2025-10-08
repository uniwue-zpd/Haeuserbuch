package de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PlaceProperties extends Properties {
    private String realName;
    private Set<String> altNames = new HashSet<>();
    private Boolean isUncertain;
    private String internalNotes;
    private String generalNotes;
}
