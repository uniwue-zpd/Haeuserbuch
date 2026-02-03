package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.CitizenshipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/citizenships")
public class CitizenshipController {
    private final CitizenshipService citizenshipService;

    public CitizenshipController(CitizenshipService citizenshipService) {
        this.citizenshipService = citizenshipService;
    }

    @GetMapping
    public ResponseEntity<List<CitizenshipDTO>> getAllCitizenships() {
        List<CitizenshipDTO> citizenships = citizenshipService.getAllCitizenships();
        return ResponseEntity.ok(citizenships);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitizenshipDTO> getCitizenshipById(@PathVariable Long id) {
        return citizenshipService.getCitizenshipById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<CitizenshipDTO> createCitizenship(@RequestBody CitizenshipDTO citizenshipDTO) {
        CitizenshipDTO createdCitizenship = citizenshipService.createCitizenship(citizenshipDTO);
        return ResponseEntity.status(201).body(createdCitizenship);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitizenshipDTO> updateCitizenship(@PathVariable Long id, @RequestBody CitizenshipDTO citizenshipDTO) {
        try {
            CitizenshipDTO citizenship = citizenshipService.updateCitizenship(id, citizenshipDTO);
            return ResponseEntity.status(200).body(citizenship);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCitizenshipById(@PathVariable Long id) {
        try {
            citizenshipService.deleteCitizenship(id);
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

}
