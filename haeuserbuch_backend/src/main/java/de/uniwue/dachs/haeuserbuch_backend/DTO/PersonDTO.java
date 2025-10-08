package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String sex;
    private String occupation;
    private String occupationCategory;
    private Boolean isCitizen;
    private String confession;
}
