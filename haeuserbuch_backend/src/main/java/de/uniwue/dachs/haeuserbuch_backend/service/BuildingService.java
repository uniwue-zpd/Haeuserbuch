package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.GeoJSONFeature;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.GeoJSONFeatureCollection;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.GeoJSONGeometry;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.GeoJSONProperties;
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
            GeoJSONProperties properties = new GeoJSONProperties();
            GeoJSONGeometry geometry = new GeoJSONGeometry();

            properties.setId(building.getId());
            properties.setName(building.getName());
            properties.setAddress(building.getAddress());
            properties.setDescription(building.getDescription());
            feature.setProperties(properties);

            geometry.setType(GeoJSONGeometry.GeometryTypes.Polygon);
            List<Double[][]> coordinates = new ArrayList<>();
            coordinates.add(convertPolygon(building.getShape()));
            geometry.setCoordinates(coordinates);
            feature.setGeometry(geometry);
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
            GeoJSONGeometry geometry = new GeoJSONGeometry();
            GeoJSONProperties properties = new GeoJSONProperties();

            properties.setId(entity_feature.getId());
            properties.setName(entity_feature.getName());
            properties.setAddress(entity_feature.getAddress());
            properties.setDescription(entity_feature.getDescription());
            feature.setProperties(properties);

            geometry.setType(GeoJSONGeometry.GeometryTypes.Polygon);
            List<Double[][]> coordinates = new ArrayList<>();
            coordinates.add(convertPolygon(entity_feature.getShape()));
            geometry.setCoordinates(coordinates);
            feature.setGeometry(geometry);

            return feature;
        });
    }

    // Create building
    @Transactional
    public Building createBuilding(BuildingDTO buildingDTO) {
        Building building = new Building();
        building.setName(buildingDTO.getName());
        building.setAddress(buildingDTO.getAddress());
        building.setDescription(buildingDTO.getDescription());
        building.setShape(createPolygon(buildingDTO.getShape()));
        return buildingRepository.save(building);
    }
}
