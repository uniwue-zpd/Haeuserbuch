package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.PlaceRegisterDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.PlaceService;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.FeatureCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/places")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<FeatureCollection> getBuildings() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feature> getPlaceById(@PathVariable Long id) {
        return placeService.getPlaceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Feature> createPlace(@RequestBody Feature feature) {
        Feature createdPlace = placeService.createPlace(feature);
        return ResponseEntity.status(201).body(createdPlace);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feature> updatePlace(@PathVariable Long id, @RequestBody Feature feature) {
        try {
            Feature updatedPlace = placeService.updatePlace(id, feature);
            return ResponseEntity.status(200).body(updatedPlace);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        try {
            placeService.deletePlace(id);
            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlaceDTO>> searchPlaces(@RequestParam String query) {
        if (query == null || query.trim().length() < 3) {
            return ResponseEntity.ok(List.of());
        }
        List<PlaceDTO> results = placeService.searchPlaces(query);
        return ResponseEntity.ok(results);
    }

    /**
     * Place register: paged places with their related entries, filtered by real or alternative place name.
     * {@code type} is either "persons" (origin/emigration places of persons) or "citizenships" (citizenships whose citizen, guarantor or mentioned person has the place as origin/emigration place).
     */
    @GetMapping("/register")
    public ResponseEntity<Page<? extends PlaceRegisterDTO<?>>> getRegister(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "name", required = false) String name,
            Pageable pageable
    ) {
        return switch (type) {
            case "persons" -> ResponseEntity.ok(placeService.getPersonRegister(name, pageable));
            case "citizenships" -> ResponseEntity.ok(placeService.getCitizenshipRegister(name, pageable));
            default -> ResponseEntity.badRequest().build();
        };
    }
}
