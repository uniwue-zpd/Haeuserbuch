package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.model.Quarter;
import de.uniwue.dachs.haeuserbuch_backend.service.QuarterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quarters")
public class QuarterController {
    private final QuarterService quarterService;

    public QuarterController(QuarterService quarterService) {
        this.quarterService = quarterService;
    }

    @GetMapping
    public ResponseEntity<List<Quarter>> getQuarters() {
        List<Quarter> quarters = quarterService.getAllQuarters();
        return ResponseEntity.ok(quarters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quarter> getQuarterById(@PathVariable Long id) {
        return quarterService.getQuarterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Quarter> createQuarter(@RequestBody Quarter quarter) {
        Quarter createdQuarter = quarterService.createQuarter(quarter);
        return ResponseEntity.status(201).body(createdQuarter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Quarter> updateQuarter(@PathVariable Long id, @RequestBody Quarter updatedQuarter) {
        Quarter quarter = quarterService.updateQuarter(id, updatedQuarter);
        if (quarter != null) {
            return ResponseEntity.status(201).body(quarter);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuarter(@PathVariable Long id) {
        try {
            quarterService.deleteQuarter(id);
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
