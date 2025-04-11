package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/buildings")
public class BuildingController {
    private final BuildingRepository buildingRepository;

    public BuildingController(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @GetMapping
    public ResponseEntity<Iterable<Building>> getBuildings() {
        Iterable<Building> buildings = buildingRepository.findAll();
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuilding(@PathVariable long id) {
        return buildingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Building> saveBuilding(@RequestBody Building building) {
        Building savedBuilding = buildingRepository.save(building);
        return ResponseEntity.status(201).body(savedBuilding);
    }

    // TODO: Implement other mapping and move functionality to service layer
    // TODO: Consider the usage of DTO projections
}
