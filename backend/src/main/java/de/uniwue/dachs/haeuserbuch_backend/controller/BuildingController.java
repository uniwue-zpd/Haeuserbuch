package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.BuildingService;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.FeatureCollection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/buildings")
public class BuildingController {
    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    /**
     * GET buildings as GeoJSON ({@code view=geojson} or no view), all or filtered.
     */
    @GetMapping
    public ResponseEntity<FeatureCollection> getBuildings(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, value = "district-id") Long districtId,
            @RequestParam(required = false, value = "quarter-id") Long quarterId,
            @RequestParam(required = false, value = "street-id") Long streetId,
            @RequestParam(required = false, value = "source-id") Long sourceId,
            @RequestParam(required = false) Boolean georeferenced
    ) {
        return ResponseEntity.ok(
                buildingService.getBuildings(q, districtId, quarterId, streetId, sourceId, georeferenced)
        );
    }

    /**
     * GET buildings as lightweight previews ({@code view=preview}), all or filtered.
     */
    @GetMapping(params = "view=preview")
    public ResponseEntity<List<BuildingDTO>> getBuildingPreviews(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, value = "district-id") Long districtId,
            @RequestParam(required = false, value = "quarter-id") Long quarterId,
            @RequestParam(required = false, value = "street-id") Long streetId,
            @RequestParam(required = false, value = "source-id") Long sourceId,
            @RequestParam(required = false) Boolean georeferenced
    ) {
        return ResponseEntity.ok(
                buildingService.getBuildingPreviews(q, districtId, quarterId, streetId, sourceId, georeferenced)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feature> getBuildingById(@PathVariable Long id) {
        return buildingService.getBuildingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Feature> createBuilding(@RequestBody Feature feature) {
        try {
            Feature createdFeature = buildingService.createBuilding(feature);
            return ResponseEntity.status(201).body(createdFeature);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feature> updateBuilding(@PathVariable Long id, @RequestBody Feature feature) {
        try {
            Feature updatedFeature = buildingService.updateBuilding(id, feature);
            return ResponseEntity.status(200).body(updatedFeature);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        try {
            buildingService.deleteBuilding(id);
            return ResponseEntity.status(204).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<BuildingDTO>> searchBuildings(@RequestParam String query) {
        if (query == null || query.trim().length() < 3) {
            return ResponseEntity.ok(List.of());
        }
        List<BuildingDTO> results = buildingService.searchBuildings(query);
        return ResponseEntity.ok(results);

    }
}
