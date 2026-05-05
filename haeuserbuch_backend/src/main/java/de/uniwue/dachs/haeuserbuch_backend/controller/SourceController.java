package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.SourceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.service.SourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sources")
public class SourceController {
    private final SourceService sourceService;

    public SourceController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @GetMapping
    public ResponseEntity<List<Source>> getAllSources() {
        List<Source> sources = sourceService.getAllSources();
        return ResponseEntity.ok(sources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Source> getSourceById(@PathVariable Long id) {
        return sourceService.getSourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Source> createSource(@RequestBody Source source) {
        Source createdSource = sourceService.createSource(source);
        return ResponseEntity.status(201).body(createdSource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Source> updateSource(@PathVariable Long id, @RequestBody Source updatedSource) {
        Source source = sourceService.updateSource(id, updatedSource);
        if (source != null) {
            return ResponseEntity.status(201).body(source);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSource(@PathVariable Long id) {
        try {
            sourceService.deleteSource(id);
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<SourceDTO>> searchSources(@RequestParam String query) {
        if (query == null || query.trim().length() < 3) {
            return ResponseEntity.ok(List.of());
        }
        List<SourceDTO> results = sourceService.searchSources(query);
        return ResponseEntity.ok(results);
    }
}
