package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.CitizenshipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/all")
    public ResponseEntity<List<CitizenshipDTO>> getAllCitizenships() {
        List<CitizenshipDTO> citizenships = citizenshipService.getAllCitizenships();
        return ResponseEntity.ok(citizenships);
    }

    @GetMapping
    public ResponseEntity<Page<CitizenshipDTO>> getPagedCitizenships(
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable,
            @RequestParam(required = false, value="refnumber") String refNumber,
            @RequestParam(required = false) String signature,
            @RequestParam(required = false, value="naturalizedperson") String naturalizedPerson,
            @RequestParam(required = false, value="datenaturalization") String dateNaturalization,
            @RequestParam(required = false, value="primarySource") String primarySource,
            @RequestParam(required = false, value="secondarySource") String secondarySource
    ) {
        Page<CitizenshipDTO> citizenships = citizenshipService
                .getPagedCitizenships(pageable, refNumber, signature, naturalizedPerson, dateNaturalization, primarySource, secondarySource);
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
