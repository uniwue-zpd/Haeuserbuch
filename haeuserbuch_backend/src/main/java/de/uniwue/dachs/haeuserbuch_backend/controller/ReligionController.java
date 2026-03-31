package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.model.Religion;
import de.uniwue.dachs.haeuserbuch_backend.service.ReligionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/religions")
public class ReligionController {
    private final ReligionService religionService;

    public ReligionController(ReligionService religionService) {
        this.religionService = religionService;
    }

    @GetMapping
    public ResponseEntity<List<Religion>> getReligions() {
        List<Religion> religions = religionService.getAllReligions();
        return ResponseEntity.ok(religions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Religion> getReligionById(@PathVariable Long id) {
        return religionService.getReligionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Religion> createReligion(@RequestBody Religion religion) {
        Religion createdReligion = religionService.createReligion(religion);
        return ResponseEntity.status(201).body(createdReligion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Religion> updateReligion(@PathVariable Long id, @RequestBody Religion updatedReligion) {
        try {
            Religion religion = religionService.updateReligion(id, updatedReligion);
            return ResponseEntity.ok(religion);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReligion(@PathVariable Long id) {
        try {
            religionService.deleteReligion(id);
            return ResponseEntity.status(204).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
