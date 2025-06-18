package de.uniwue.dachs.haeuserbuch_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.BuildingService;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.Feature;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/buildings")
public class BuildingController {
    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<?> getBuildings(
            @RequestParam(required = false, defaultValue = "json") String output) {
        return output.equalsIgnoreCase("geojson")
                ? ResponseEntity.ok(buildingService.getAllBuildingFeatures())
                : ResponseEntity.ok(buildingService.getAllBuildings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBuildingById(@PathVariable long id,
                                             @RequestParam(required = false, defaultValue = "json") String output) {
        return output.equalsIgnoreCase("geojson")
                ? buildingService.getBuildingFeatureById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(404).build())
                : buildingService.getBuildingById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Void> saveBuilding(
            @RequestParam(required = false, defaultValue = "json") String input,
            @RequestBody Map<String, Object> payload) {
        try {
            if (input.equalsIgnoreCase("geojson")) {
                Feature geoJSONFeature = new ObjectMapper().convertValue(payload, Feature.class);
                buildingService.createBuildingFromGeoJSON(geoJSONFeature);
            } else {
                BuildingDTO buildingDTO = new ObjectMapper().convertValue(payload, BuildingDTO.class);
                buildingService.createBuilding(buildingDTO);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBuilding(
            @RequestParam(required = false, defaultValue = "json") String input,
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            if (input.equalsIgnoreCase("geojson")) {
                Feature geoJSONFeature = new ObjectMapper().convertValue(payload, Feature.class);
                buildingService.updateBuildingFromGeoJSON(id, geoJSONFeature);
            } else {
                BuildingDTO buildingDTO = new ObjectMapper().convertValue(payload, BuildingDTO.class);
                buildingService.updateBuilding(id, buildingDTO);
            }
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
