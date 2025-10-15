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
    public ResponseEntity<List<BuildingDTO>> getBuildingsBy(@RequestParam(required = false) Long districtId,
                                                            @RequestParam(required = false) Long quarterId,
                                                            @RequestParam(required = false) Long streetId)
    {
        List<Long> paramsCount = Stream.of(districtId, quarterId, streetId).filter(Objects::nonNull).toList();
        if (paramsCount.size() != 1) return ResponseEntity.badRequest().build();

        if (districtId != null) return ResponseEntity.ok(buildingService.getBuildingsByDistrictId(districtId));
        if (quarterId != null) return ResponseEntity.ok(buildingService.getBuildingsByQuarterId(quarterId));
        if (streetId != null) return ResponseEntity.ok(buildingService.getBuildingsByStreetId(streetId));
        return ResponseEntity.badRequest().build();
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
