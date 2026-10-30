package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonEmigrationDTO {
    private String originalText;
    private PlaceDTO place;
}
