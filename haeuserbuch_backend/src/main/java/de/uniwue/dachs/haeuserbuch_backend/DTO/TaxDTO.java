package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaxDTO {
    private Long id;
    private String tax_number;
    private String plan_number;
    private String entry_text;
    private Feature building;
    private Person person;
    private Source source;
    private String notes;
}
