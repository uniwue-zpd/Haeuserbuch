package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonJobDTO {
    private String originalText;
    private JobDTO jobCategory;
}
