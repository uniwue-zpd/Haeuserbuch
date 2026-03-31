package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.WeaponryDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Weaponry;
import de.uniwue.dachs.haeuserbuch_backend.repository.WeaponryRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class WeaponryMapper {
    private final WeaponryRepository weaponryRepository;
    private final WeaponMapper weaponMapper;

    public WeaponryMapper(WeaponryRepository weaponryRepository, WeaponMapper weaponMapper) {
        this.weaponryRepository = weaponryRepository;
        this.weaponMapper = weaponMapper;
    }

    public Weaponry DTOToWeaponry(WeaponryDTO weaponryDTO) {
        if (weaponryDTO == null) return null;
        if (weaponryDTO.getId() != null) {
            return weaponryRepository.findById(weaponryDTO.getId())
                    .map(existing -> {
                        existing.setWeapon(weaponMapper.DTOToWeapon(weaponryDTO.getWeapon()));
                        existing.setOriginalText(weaponryDTO.getOriginalText());
                        return existing;
                    }).orElse(null);
        } else {
            Weaponry weaponry = new Weaponry();
            weaponry.setWeapon(weaponMapper.DTOToWeapon(weaponryDTO.getWeapon()));
            weaponry.setOriginalText(weaponryDTO.getOriginalText());
            return weaponry;
        }
    }

    public Set<Weaponry> DTOsToWeaponries(Set<WeaponryDTO> weaponryDTOs) {
        if (weaponryDTOs == null) return null;
        return weaponryDTOs.stream().map(this::DTOToWeaponry).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public WeaponryDTO WeaponryToDTO(Weaponry weaponry) {
        if (weaponry == null) return null;
        WeaponryDTO weaponryDTO = new WeaponryDTO();
        weaponryDTO.setId(weaponry.getId());
        weaponryDTO.setWeapon(weaponMapper.WeaponToDTO(weaponry.getWeapon()));
        weaponryDTO.setOriginalText(weaponry.getOriginalText());
        return weaponryDTO;
    }

    public Set<WeaponryDTO> WeaponriesToDTOs(Set<Weaponry> weaponries) {
        if (weaponries == null) return null;
        return weaponries.stream().map(this::WeaponryToDTO).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
