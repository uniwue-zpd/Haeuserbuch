package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.model.Weapon;
import de.uniwue.dachs.haeuserbuch_backend.service.WeaponService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weapons")
public class WeaponController {
    private final WeaponService weaponService;

    public WeaponController(WeaponService weaponService) {
        this.weaponService = weaponService;
    }

    @GetMapping
    public ResponseEntity<List<Weapon>> getWeapons() {
        List<Weapon> weapons = weaponService.getAllWeapons();
        return ResponseEntity.ok(weapons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Weapon> getWeaponById(@PathVariable Long id) {
        return weaponService.getWeaponById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Weapon> createWeapon(@RequestBody Weapon weapon) {
        Weapon createdWeapon = weaponService.createWeapon(weapon);
        return ResponseEntity.status(201).body(createdWeapon);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Weapon> updateWeapon(@PathVariable Long id, @RequestBody Weapon updatedWeapon) {
        try {
            Weapon weapon = weaponService.updateWeapon(id, updatedWeapon);
            return ResponseEntity.ok(weapon);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeapon(@PathVariable Long id) {
        try {
            weaponService.deleteWeapon(id);
            return ResponseEntity.status(204).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
