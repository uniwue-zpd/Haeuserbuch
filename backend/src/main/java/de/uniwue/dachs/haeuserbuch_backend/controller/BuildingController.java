package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.BuildingService;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.FeatureCollection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;


@RestController
@RequestMapping("/buildings")
public class BuildingController {
    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<FeatureCollection> getBuildings() {
        return ResponseEntity.ok(buildingService.getAllBuildings());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<BuildingDTO>> searchBuildings(
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) String districtName,
            @RequestParam(required = false) Long quarterId,
            @RequestParam(required = false) String quarterName,
            @RequestParam(required = false) Long sourceId,
            @RequestParam(required = false) String sourceName,
            @RequestParam(required = false) Long streetId,
            @RequestParam(required = false) String streetName
    ) {
        List<Object> paramsCount = Stream.<Object>of(
                districtId, districtName, quarterId, quarterName, sourceId, sourceName, streetId, streetName
                )
                .filter(Objects::nonNull).toList();
        if (paramsCount.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(
                buildingService.searchBuildings(
                        districtId, districtName, quarterId, quarterName, sourceId, sourceName, streetId, streetName
                )
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
