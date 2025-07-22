package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.OwnershipDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.OwnershipService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ownerships")
public class OwnershipController {
    private final OwnershipService ownershipService;

    public OwnershipController(OwnershipService ownershipService) {
        this.ownershipService = ownershipService;
    }

    @GetMapping
    public ResponseEntity<List<OwnershipDTO>> getAllOwnerships() {
        List<OwnershipDTO> ownerships = ownershipService.getAllOwnerships();
        return  ResponseEntity.ok(ownerships);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnershipDTO> getOwnershipById(@PathVariable Long id) {
        return ownershipService.getOwnershipById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Void> createOwnership(@RequestBody OwnershipDTO ownershipDTO) {
        try {
            ownershipService.createOwnership(ownershipDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOwnership(@PathVariable Long id, @RequestBody OwnershipDTO ownershipDTO) {
        try {
            ownershipService.updateOwnership(id, ownershipDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwnership(@PathVariable Long id) {
        try {
            ownershipService.deleteOwnership(id);
            return ResponseEntity.status(204).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
