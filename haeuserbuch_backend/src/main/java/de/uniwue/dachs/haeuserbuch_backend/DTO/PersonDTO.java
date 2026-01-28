package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PersonDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private Set<String> altNames = new HashSet<>();
    private String sex;
    private String occupation;
    private String occupationCategory;
    private BuildingDTO associatedBuilding;
    private Boolean isCitizen;
    private String confession;
    private PersonOriginDTO origin;
    private String internalNotes;
    private String generalNotes;
}
