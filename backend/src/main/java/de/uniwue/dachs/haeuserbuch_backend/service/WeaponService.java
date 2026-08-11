package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.WeaponDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Weapon;
import de.uniwue.dachs.haeuserbuch_backend.repository.WeaponRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.WeaponMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class WeaponService {
    private final WeaponRepository weaponRepository;
    private final WeaponMapper weaponMapper;

    public WeaponService(WeaponRepository weaponRepository, WeaponMapper weaponMapper) {
        this.weaponRepository = weaponRepository;
        this.weaponMapper = weaponMapper;
    }

    /**
     * GET all weapons
     * @return {@link List} of all {@link Weapon} objects
     */
    public List<Weapon> getAllWeapons() {
        return weaponRepository.findAll();
    }

    /**
     * GET weapon by ID
     * @param id of the weapon
     * @return {@link Optional} of {@link Weapon}
     */
    public Optional<Weapon> getWeaponById(Long id) {
        return weaponRepository.findById(id);
    }

    /**
     * POST create a new weapon
     * @param weapon {@link Weapon} to be created
     * @return the created {@link Weapon}
     */
    @Transactional
    @SearchIndexAffecting
    public Weapon createWeapon(Weapon weapon) {
        return weaponRepository.save(weapon);
    }

    /**
     * PUT update an existing weapon
     * @param weapon {@link Weapon} with updated data
     * @return the updated {@link Weapon}
     * @throws EntityNotFoundException if the weapon with the given ID does not exist
     */
    @Transactional
    @SearchIndexAffecting
    public Weapon updateWeapon(Long id, Weapon weapon) {
        return weaponRepository.findById(id)
                .map(existingEntity -> {
                    existingEntity.setName(weapon.getName());
                    existingEntity.setDescription(weapon.getDescription());
                    return weaponRepository.save(existingEntity);
                })
                .orElseThrow(() -> new EntityNotFoundException("Weapon with ID " + id + " not found"));
    }

    /**
     * DELETE weapon by ID
     * @param id of the weapon to be deleted
     * @throws EntityNotFoundException if the weapon with the given ID does not exist
     */
    @Transactional
    @SearchIndexAffecting
    public void deleteWeapon(Long id) {
        if (!weaponRepository.existsById(id)) {
            throw new EntityNotFoundException("Weapon with ID " + id + " not found");
        }
        weaponRepository.deleteById(id);
    }

    /**
     * Allows searching for weapons based on a search term.
     * @param query Search term.
     * @return A {@link List} of {@link WeaponDTO} objects that match the search criteria.
     */
    public List<WeaponDTO> searchWeapons(String query) {
        return weaponRepository.searchWeapons(query).stream()
                .map(weaponMapper::WeaponToDTO)
                .filter(Objects::nonNull)
                .toList();
    }
}
