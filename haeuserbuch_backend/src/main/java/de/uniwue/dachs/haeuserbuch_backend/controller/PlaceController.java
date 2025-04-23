package de.uniwue.dachs.haeuserbuch_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PlaceDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.PlaceService;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.GeoJSONFeature;
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
                GeoJSONFeature geoJSONFeature = new ObjectMapper().convertValue(payload, GeoJSONFeature.class);
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

    // TODO: Implement other mapping and move functionality to service layer
    // TODO: Consider the usage of DTO projections
}
