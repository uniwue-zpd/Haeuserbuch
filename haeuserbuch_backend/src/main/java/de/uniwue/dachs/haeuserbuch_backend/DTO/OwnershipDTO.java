package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class OwnershipDTO {
    private Long id;
    private String type;
    private String date;
    private Long price;
    private Person owner;
    private Person seller;
    private Set<Feature> buildings;
    private Source source;
    private String entry_text;
    private String notes;
}
