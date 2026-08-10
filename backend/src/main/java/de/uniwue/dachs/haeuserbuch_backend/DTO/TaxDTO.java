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
    private String taxNumber;
    private String planNumber;
    private String entryText;
    private Feature building;
    private Person person;
    private Source source;
    private String internalNotes;
    private String generalNotes;
}
