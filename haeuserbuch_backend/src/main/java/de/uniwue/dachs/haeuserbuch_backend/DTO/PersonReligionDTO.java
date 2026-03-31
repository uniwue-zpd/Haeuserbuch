package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonReligionDTO {
    private String originalText;
    private ReligionDTO religionCategory;
}
