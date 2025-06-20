package de.uniwue.dachs.haeuserbuch_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.PlaceService;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.Feature;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<?> getPlaces(
            @RequestParam(required = false, defaultValue = "json") String output) {
        return output.equalsIgnoreCase("geojson")
                ? ResponseEntity.ok(placeService.getAllPlaceFeatures())
                : ResponseEntity.ok(placeService.getAllPlaces());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlaceById(@PathVariable Long id,
                                          @RequestParam(required = false, defaultValue = "json") String output) {
        return output.equalsIgnoreCase("geojson")
                ? placeService.getPlaceFeatureById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(404).build())
                : placeService.getPlaceById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(404).build());
    }

    @PostMapping
    public ResponseEntity<Void> savePlace(
            @RequestParam(required = false, defaultValue = "json") String input,
            @RequestBody Map<String, Object> payload) {
        try {
            if (input.equalsIgnoreCase("geojson")) {
                Feature geoJSONFeature = new ObjectMapper().convertValue(payload, Feature.class);
                placeService.createPlaceFromGeoJSON(geoJSONFeature);
            } else {
                PlaceDTO placeDTO = new ObjectMapper().convertValue(payload, PlaceDTO.class);
                placeService.createPlace(placeDTO);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePlace(
            @RequestParam(required = false, defaultValue = "json") String input,
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload) {
        try {
            if (input.equalsIgnoreCase("geojson")) {
                Feature geoJSONFeature = new ObjectMapper().convertValue(payload, Feature.class);
                placeService.updatePlaceFromGeoJSON(id, geoJSONFeature);
            } else {
                PlaceDTO placeDTO = new ObjectMapper().convertValue(payload, PlaceDTO.class);
                placeService.updatePlace(id, placeDTO);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok().build();
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
}
