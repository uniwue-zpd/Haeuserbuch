package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class PersonWeaponsDTO {
    private Set<String> originalNames = new HashSet<>();
    private Set<WeaponDTO> weaponCategories = new HashSet<>();
}
