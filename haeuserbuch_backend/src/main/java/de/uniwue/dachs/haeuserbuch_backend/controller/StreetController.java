package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.model.Street;
import de.uniwue.dachs.haeuserbuch_backend.service.StreetService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/streets")
public class StreetController {
    private final StreetService streetService;

    public StreetController(StreetService streetService) {
        this.streetService = streetService;
    }

    @GetMapping
    public ResponseEntity<List<Street>> getStreets() {
        List<Street> streets = streetService.getAllStreets();
        return ResponseEntity.ok(streets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Street> getStreetById(@PathVariable Long id) {
        return streetService.getStreetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Street> createStreet(@RequestBody Street street) {
        Street createdStreet = streetService.createStreet(street);
        return ResponseEntity.status(201).body(createdStreet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Street> updateStreet(@PathVariable Long id, @RequestBody Street updatedStreet) {
        try {
            Street street = streetService.updateStreet(id, updatedStreet);
            return ResponseEntity.ok(street);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStreet(@PathVariable Long id) {
        try {
            streetService.deleteStreet(id);
            return ResponseEntity.status(204).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
