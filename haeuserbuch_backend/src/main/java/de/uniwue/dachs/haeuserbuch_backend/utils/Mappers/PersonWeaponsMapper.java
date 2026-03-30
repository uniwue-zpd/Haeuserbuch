package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PersonWeaponsDTO;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.PersonWeapons;
import org.springframework.stereotype.Component;

@Component
public class PersonWeaponsMapper {
    private final WeaponMapper weaponMapper;

    public PersonWeaponsMapper(WeaponMapper weaponMapper) {
        this.weaponMapper = weaponMapper;
    }

    public PersonWeapons DTOToPersonWeapons(PersonWeaponsDTO personWeaponsDTO) {
        if (personWeaponsDTO == null) return new PersonWeapons();
        PersonWeapons personWeapons = new PersonWeapons();
        personWeapons.setWeaponCategories(weaponMapper.DTOsToWeapons(personWeaponsDTO.getWeaponCategories()));
        personWeapons.setOriginalNames(personWeaponsDTO.getOriginalNames());
        return personWeapons;
    }

    public PersonWeaponsDTO PersonWeaponsToDTO(PersonWeapons personWeapons) {
        if (personWeapons == null) return new PersonWeaponsDTO();
        PersonWeaponsDTO personWeaponsDTO = new PersonWeaponsDTO();
        personWeaponsDTO.setWeaponCategories(weaponMapper.WeaponsToDTOs(personWeapons.getWeaponCategories()));
        personWeaponsDTO.setOriginalNames(personWeapons.getOriginalNames());
        return personWeaponsDTO;
    }
}
