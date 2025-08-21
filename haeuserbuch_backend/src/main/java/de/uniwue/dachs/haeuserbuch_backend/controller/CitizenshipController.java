package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
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
    public ResponseEntity<Citizenship> createCitizenship(@RequestBody CitizenshipDTO citizenshipDTO) {
        citizenshipService.createCitizenship(citizenshipDTO);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCitizenship(@PathVariable Long id, @RequestBody CitizenshipDTO citizenshipDTO) {
        try {
            citizenshipService.updateCitizenship(id, citizenshipDTO);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok().build();
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
