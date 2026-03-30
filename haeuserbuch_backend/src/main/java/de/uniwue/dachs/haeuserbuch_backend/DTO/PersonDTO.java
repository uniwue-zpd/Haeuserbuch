package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PersonDTO extends BaseEntity {
    private String firstName;
    private String lastName;
    private String fullName;
    private Set<String> altNames = new HashSet<>();
    private String sex;
    private PersonOccupationDTO occupation;
    private BuildingDTO associatedBuilding;
    private Boolean isCitizen;
    private String confession;
    private PersonWeaponsDTO weapons;
    private PersonOriginDTO origin;
    private String internalNotes;
    private String generalNotes;
}
