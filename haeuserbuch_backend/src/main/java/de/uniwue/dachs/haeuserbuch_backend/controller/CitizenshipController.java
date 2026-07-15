package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.FullTextSearch.CitizenshipFullTextSearchResult;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.CitizenshipPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.CitizenshipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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
            @RequestParam(required = false, value="naturalizedperson-id") Long naturalizedPersonId,
            @RequestParam(required = false, value="datenaturalization") String dateNaturalization,
            @RequestParam(required = false, value="primarysource") String primarySource,
            @RequestParam(required = false, value="secondarysource") String secondarySource
    ) {
        Page<CitizenshipDTO> citizenships = citizenshipService
                .getPagedCitizenships(pageable, refNumber, signature, naturalizedPerson, naturalizedPersonId, dateNaturalization, primarySource, secondarySource);
        return ResponseEntity.ok(citizenships);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CitizenshipPreviewDTO>> getFilteredCitizenships(
            @RequestParam(required = false, value="refnumber") String refNumber,
            @RequestParam(required = false) String signature,
            @RequestParam(required = false, value="naturalizedperson") String naturalizedPerson,
            @RequestParam(required = false, value="naturalizedperson-id") Long naturalizedPersonId,
            @RequestParam(required = false, value="datenaturalization") String dateNaturalization,
            @RequestParam(required = false, value="primarysource") String primarySource,
            @RequestParam(required = false, value="secondarysource") String secondarySource
    ) {
        List<Object> paramsCount = Stream.<Object>of(refNumber, signature, naturalizedPerson, naturalizedPersonId, dateNaturalization, primarySource, secondarySource)
                .filter(Objects::nonNull).toList();
        if (paramsCount.isEmpty()) return ResponseEntity.badRequest().build();
        List<CitizenshipPreviewDTO> citizenships = citizenshipService.filterCitizenships(
                refNumber, signature, naturalizedPerson, naturalizedPersonId, dateNaturalization, primarySource, secondarySource
        );
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

    @GetMapping("/fulltextsearch")
    public ResponseEntity<Page<CitizenshipFullTextSearchResult>> searchCitizenshipFulltext(
            @RequestParam String query,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<CitizenshipFullTextSearchResult> results = citizenshipService.searchCitizenshipFullText(query, pageable);
        return ResponseEntity.ok(results);
    }
}
