package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.model.Occupation;
import de.uniwue.dachs.haeuserbuch_backend.service.OccupationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/occupations")
public class OccupationController {
    private final OccupationService occupationService;

    public OccupationController(OccupationService occupationService) {
        this.occupationService = occupationService;
    }

    @GetMapping
    public ResponseEntity<List<Occupation>> getOccupations() {
        List<Occupation> occupations = occupationService.getAllOccupations();
        return ResponseEntity.ok(occupations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Occupation> getOccupationById(@PathVariable Long id) {
        return occupationService.getOccupationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Occupation> createOccupation(@RequestBody Occupation occupation) {
        Occupation createdOccupation = occupationService.createOccupation(occupation);
        return ResponseEntity.status(201).body(createdOccupation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Occupation> updateOccupation(@PathVariable Long id, @RequestBody Occupation updatedOccupation) {
        try {
            Occupation occupation = occupationService.updateOccupation(id, updatedOccupation);
            return ResponseEntity.ok(occupation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOccupation(@PathVariable Long id) {
        try {
            occupationService.deleteOccupation(id);
            return ResponseEntity.status(204).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
