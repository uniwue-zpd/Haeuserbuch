package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.GeoJSONFeature;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.GeoJSONFeatureCollection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.convertPolygon;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPolygon;

@Service
public class BuildingService {
    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    // Get all buildings
    public List<BuildingDTO> getAllBuildings() {
        List<Building> buildings = buildingRepository.findAll();
        List<BuildingDTO> buildingDTOs = new ArrayList<>();
        for (Building building : buildings) {
            BuildingDTO current_dto = new BuildingDTO();
            current_dto.setId(building.getId());
            current_dto.setName(building.getName());
            current_dto.setAddress(building.getAddress());
            current_dto.setDescription(building.getDescription());
            current_dto.setShape(convertPolygon(building.getShape()));
            buildingDTOs.add(current_dto);
        }
        return buildingDTOs;
    }

    // Get all buildings as feature collection
    public GeoJSONFeatureCollection getAllBuildingFeatures() {
        List<Building> buildings = buildingRepository.findAll();
        GeoJSONFeatureCollection featureCollection = new GeoJSONFeatureCollection();
        List<GeoJSONFeature> features = new ArrayList<>();
        for (Building building : buildings) {
            GeoJSONFeature feature = new GeoJSONFeature();

            feature.getProperties().put("id", building.getId());
            feature.getProperties().put("name", building.getName());
            feature.getProperties().put("address", building.getAddress());
            feature.getProperties().put("description", building.getDescription());

            feature.getGeometry().put("type", "Polygon");
            List<Double[][]> coordinates = new ArrayList<>();
            coordinates.add(convertPolygon(building.getShape()));
            feature.getGeometry().put("coordinates", coordinates);
            features.add(feature);
        }
        featureCollection.setFeatures(features);
        return featureCollection;
    }

    // Get building by its id
    public Optional<BuildingDTO> getBuildingById(Long id) {
        return buildingRepository.findById(id).map(entity -> {
            BuildingDTO current_dto = new BuildingDTO();
            current_dto.setId(entity.getId());
            current_dto.setName(entity.getName());
            current_dto.setAddress(entity.getAddress());
            current_dto.setDescription(entity.getDescription());
            current_dto.setShape(convertPolygon(entity.getShape()));
            return current_dto;
        });
    }

    // Get building by its id (geojson)
    public Optional<GeoJSONFeature> getBuildingFeatureById(Long id) {
        return buildingRepository.findById(id).map(entity_feature -> {
            GeoJSONFeature feature = new GeoJSONFeature();

            feature.getProperties().put("id", entity_feature.getId());
            feature.getProperties().put("name", entity_feature.getName());
            feature.getProperties().put("address", entity_feature.getAddress());
            feature.getProperties().put("description", entity_feature.getDescription());

            feature.getGeometry().put("type", "Polygon");
            List<Double[][]> coordinates = new ArrayList<>();
            coordinates.add(convertPolygon(entity_feature.getShape()));
            feature.getGeometry().put("coordinates", coordinates);

            return feature;
        });
    }

    // Create building from JSON
    @Transactional
    public Building createBuilding(BuildingDTO buildingDTO) {
        Building building = new Building();
        building.setName(buildingDTO.getName());
        building.setAddress(buildingDTO.getAddress());
        building.setDescription(buildingDTO.getDescription());
        building.setShape(createPolygon(buildingDTO.getShape()));
        return buildingRepository.save(building);
    }

    // Create building from GeoJSON
    @Transactional
    public Building createBuildingFromGeoJSON(GeoJSONFeature geoJSONFeature) {
        Building building = new Building();
        building.setName((String) geoJSONFeature.getProperties().get("name"));
        building.setAddress((String) geoJSONFeature.getProperties().get("address"));
        building.setDescription((String) geoJSONFeature.getProperties().get("description"));
        List<List<List<Double>>> geometry_coords = (List<List<List<Double>>>) geoJSONFeature.getGeometry().get("coordinates");
        Double[][] coordinates = geometry_coords.getFirst().stream()
                .map(innerList -> innerList.toArray(new Double[0]))
                .toArray(Double[][]::new);
        building.setShape(createPolygon(coordinates));
        return buildingRepository.save(building);
    }
}
