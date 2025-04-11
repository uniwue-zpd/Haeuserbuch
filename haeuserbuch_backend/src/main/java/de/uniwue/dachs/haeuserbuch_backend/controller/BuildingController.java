package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.BuildingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/buildings")
public class BuildingController {
    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<List<BuildingDTO>> getBuildings() {
        List<BuildingDTO> buildingDTOs = buildingService.getAllBuildings();
        return ResponseEntity.ok(buildingDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BuildingDTO> getBuildingById(@PathVariable Long id) {
        return buildingService.getBuildingById(id)
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
