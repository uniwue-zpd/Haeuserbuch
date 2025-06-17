package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CitizenshipDTO {
    private Long id;
    private Person person;
    private Source source;
    private PlaceDTO place;
    private Long number;
    private String date;
    private String entry_text;
    private String addendum;
    private String notes;
}
