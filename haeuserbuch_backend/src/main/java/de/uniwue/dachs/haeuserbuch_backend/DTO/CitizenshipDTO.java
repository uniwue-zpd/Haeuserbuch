package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CitizenshipDTO {
    private Long id;
    private Person person;
    private Source source;
    private Feature place;
    private Long number;
    private String date;
    private String entryText;
    private String addendum;
    private String internalNotes;
    private String generalNotes;
}
