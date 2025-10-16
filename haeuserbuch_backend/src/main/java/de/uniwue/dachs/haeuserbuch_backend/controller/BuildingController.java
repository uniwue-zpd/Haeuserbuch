package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.BuildingService;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.FeatureCollection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) String districtName,
            @RequestParam(required = false) Long quarterId,
            @RequestParam(required = false) String quarterName,
            @RequestParam(required = false) Long streetId,
            @RequestParam(required = false) String streetName,
            @RequestParam(required = false) Long sourceId,
            @RequestParam(required = false) String sourceName
    ) {
        List<Object> paramsCount = Stream.<Object>of(
                name, districtId, districtName, quarterId, quarterName, streetId, streetName, sourceId, sourceName
                )
                .filter(Objects::nonNull).toList();
        if (paramsCount.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(
                buildingService.searchBuildings(
                        name, districtId, districtName, quarterId, quarterName, streetId, streetName, sourceId, sourceName
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
    public ResponseEntity<Void> createBuilding(@RequestBody Feature feature) {
        try {
            buildingService.createBuilding(feature);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBuilding(@PathVariable Long id, @RequestBody Feature feature) {
        try {
            buildingService.updateBuilding(id, feature);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long id) {
        try {
            buildingService.deleteBuilding(id);
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
