package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.model.Person;
import de.uniwue.dachs.haeuserbuch_backend.model.TownBook;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CitizenshipDTO {
    private Long id;
    private Person person;
    private TownBook townBook;
    private PlaceDTO place;
    private Long number;
    private String date;
    private String entry_text;
    private String addendum;
}
