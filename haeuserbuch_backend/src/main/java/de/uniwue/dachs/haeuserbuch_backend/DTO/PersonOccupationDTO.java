package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonOccupationDTO {
    private String originalText;
    private OccupationDTO occupationCategory;
}
