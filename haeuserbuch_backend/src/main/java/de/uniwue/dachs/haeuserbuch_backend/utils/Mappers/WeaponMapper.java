package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.WeaponDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Weapon;
import de.uniwue.dachs.haeuserbuch_backend.repository.WeaponRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
public class WeaponMapper {
    private final WeaponRepository weaponRepository;

    public WeaponMapper(WeaponRepository weaponRepository) {
        this.weaponRepository = weaponRepository;
    }

    public Weapon DTOToWeapon(WeaponDTO weaponDTO) {
        if (weaponDTO == null || weaponDTO.getId() == null) return null;
        return weaponRepository.findById(weaponDTO.getId()).orElse(null);
    }

    public Set<Weapon> DTOsToWeapons(Set<WeaponDTO> weaponDTOs) {
        if (weaponDTOs == null) return null;
        return weaponDTOs.stream()
                .map(this::DTOToWeapon)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
    }

    public WeaponDTO WeaponToDTO(Weapon weapon) {
        if (weapon == null) return null;
        WeaponDTO weaponDTO = new WeaponDTO();
        weaponDTO.setId(weapon.getId());
        weaponDTO.setName(weapon.getName());
        weaponDTO.setDescription(weapon.getDescription());
        return weaponDTO;
    }

    public Set<WeaponDTO> WeaponsToDTOs(Set<Weapon> weapons) {
        if (weapons == null) return null;
        return weapons.stream()
                .map(this::WeaponToDTO)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
    }
}
