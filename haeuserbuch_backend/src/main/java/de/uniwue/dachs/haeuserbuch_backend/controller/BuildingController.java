package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> saveBuilding(@RequestBody BuildingDTO buildingDTO) {
        buildingService.createBuilding(buildingDTO);
        return ResponseEntity.status(201).build();
    }

    // TODO: Implement other mapping and move functionality to service layer
}
