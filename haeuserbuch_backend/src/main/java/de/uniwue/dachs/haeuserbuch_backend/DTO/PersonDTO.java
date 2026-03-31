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
    private BuildingDTO associatedBuilding;
    private Boolean isCitizen;
    private PersonOriginDTO origin;
    private PersonOccupationDTO occupation;
    private PersonReligionDTO religion;
    private Set<WeaponryDTO> weapons = new HashSet<>();
}
