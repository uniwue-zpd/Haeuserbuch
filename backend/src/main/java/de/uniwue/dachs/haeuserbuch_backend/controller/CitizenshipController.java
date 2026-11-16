package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.CitizenshipDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.FullTextSearch.CitizenshipFullTextSearchResult;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.CitizenshipPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.CitizenshipService;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchMatching;
import de.uniwue.dachs.haeuserbuch_backend.specification.CitizenshipSearchCriteria;
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

    /**
     * Paginated list of full {@link CitizenshipDTO} objects. All filters are optional.
     */
    @GetMapping
    public ResponseEntity<Page<CitizenshipDTO>> getPagedCitizenships(
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable,
            @RequestParam(required = false, value="refnumber") String refNumber,
            @RequestParam(required = false) String signature,
            @RequestParam(required = false, value="naturalizedperson") String naturalizedPerson,
            @RequestParam(required = false, value="naturalizedperson-id") Long naturalizedPersonId,
            @RequestParam(required = false, value="naturalizedperson-job") String naturalizedPersonJob,
            @RequestParam(required = false, value="naturalizedperson-origin") String naturalizedPersonOrigin,
            @RequestParam(required = false) String guarantor,
            @RequestParam(required = false, value="guarantor-id") Long guarantorId,
            @RequestParam(required = false, value="guarantor-job") String guarantorJob,
            @RequestParam(required = false, value="guarantor-origin") String guarantorOrigin,
            @RequestParam(required = false, value="yearnaturalization") Integer yearNaturalization,
            @RequestParam(required = false, value="year-from") Integer yearFrom,
            @RequestParam(required = false, value="year-to") Integer yearTo,
            @RequestParam(required = false, value="primarysource") String primarySource,
            @RequestParam(required = false, value="secondarysource") String secondarySource
    ) {
        CitizenshipSearchCriteria criteria = new CitizenshipSearchCriteria(
                refNumber, signature,
                naturalizedPerson, naturalizedPersonId, naturalizedPersonJob, naturalizedPersonOrigin,
                guarantor, guarantorId, guarantorJob, guarantorOrigin,
                yearNaturalization, yearFrom, yearTo, primarySource, secondarySource
        );
        return ResponseEntity.ok(citizenshipService.getPagedCitizenships(criteria, pageable));
    }

    /**
     * Unpaginated list of lightweight {@link CitizenshipPreviewDTO} objects. At least one filter is required.
     */
    @GetMapping("/filter")
    public ResponseEntity<List<CitizenshipPreviewDTO>> getFilteredCitizenships(
            @RequestParam(required = false, value="refnumber") String refNumber,
            @RequestParam(required = false) String signature,
            @RequestParam(required = false, value="naturalizedperson") String naturalizedPerson,
            @RequestParam(required = false, value="naturalizedperson-id") Long naturalizedPersonId,
            @RequestParam(required = false, value="naturalizedperson-job") String naturalizedPersonJob,
            @RequestParam(required = false, value="naturalizedperson-origin") String naturalizedPersonOrigin,
            @RequestParam(required = false) String guarantor,
            @RequestParam(required = false, value="guarantor-id") Long guarantorId,
            @RequestParam(required = false, value="guarantor-job") String guarantorJob,
            @RequestParam(required = false, value="guarantor-origin") String guarantorOrigin,
            @RequestParam(required = false, value="yearnaturalization") Integer yearNaturalization,
            @RequestParam(required = false, value="year-from") Integer yearFrom,
            @RequestParam(required = false, value="year-to") Integer yearTo,
            @RequestParam(required = false, value="primarysource") String primarySource,
            @RequestParam(required = false, value="secondarysource") String secondarySource
    ) {
        CitizenshipSearchCriteria criteria = new CitizenshipSearchCriteria(
                refNumber, signature,
                naturalizedPerson, naturalizedPersonId, naturalizedPersonJob, naturalizedPersonOrigin,
                guarantor, guarantorId, guarantorJob, guarantorOrigin,
                yearNaturalization, yearFrom, yearTo, primarySource, secondarySource
        );
        if (criteria.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(citizenshipService.filterCitizenships(criteria));
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
            @RequestParam(required = false) Boolean fuzzy,
            @RequestParam(defaultValue = "false") boolean exact,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<CitizenshipFullTextSearchResult> results = citizenshipService.searchCitizenshipFullText(query, SearchMatching.resolveFuzzy(fuzzy, exact), pageable);
        return ResponseEntity.ok(results);
    }
}
