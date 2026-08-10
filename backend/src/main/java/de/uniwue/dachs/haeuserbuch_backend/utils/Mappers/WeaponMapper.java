package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.WeaponDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Weapon;
import de.uniwue.dachs.haeuserbuch_backend.repository.WeaponRepository;
import org.springframework.stereotype.Component;

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

    public WeaponDTO WeaponToDTO(Weapon weapon) {
        if (weapon == null) return null;
        WeaponDTO weaponDTO = new WeaponDTO();
        weaponDTO.setId(weapon.getId());
        weaponDTO.setName(weapon.getName());
        weaponDTO.setDescription(weapon.getDescription());
        return weaponDTO;
    }
}
