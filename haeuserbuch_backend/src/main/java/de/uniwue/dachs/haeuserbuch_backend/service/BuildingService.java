package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.FeatureCollection;
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
            BuildingDTO current_dto = BuildingToDTO(building);
            buildingDTOs.add(current_dto);
        }
        return buildingDTOs;
    }

    // Get all buildings as feature collection
    public FeatureCollection getAllBuildingFeatures() {
        List<Building> buildings = buildingRepository.findAll();
        FeatureCollection featureCollection = new FeatureCollection();
        List<Feature> features = new ArrayList<>();
        for (Building building : buildings) {
            Feature feature = BuildingToGeoJson(building);
            features.add(feature);
        }
        featureCollection.setFeatures(features);
        return featureCollection;
    }

    // Get building by its id
    public Optional<BuildingDTO> getBuildingById(Long id) {
        return buildingRepository.findById(id).map(this::BuildingToDTO);
    }

    // Get building by its id (geojson)
    public Optional<Feature> getBuildingFeatureById(Long id) {
        return buildingRepository.findById(id).map(this::BuildingToGeoJson);
    }

    // Create building from JSON
    @Transactional
    public void createBuilding(BuildingDTO buildingDTO) {
        Building building = DtoToBuilding(buildingDTO);
        buildingRepository.save(building);
    }

    // Create building from GeoJSON
    @Transactional
    public void createBuildingFromGeoJSON(Feature feature) {
        Building building = GeoJsonToBuilding(feature);
        buildingRepository.save(building);
    }

    // Helper methods
    private Building DtoToBuilding(BuildingDTO buildingDTO) {
        Building building = new Building();
        building.setName(buildingDTO.getName());
        building.setAddress(buildingDTO.getAddress());
        building.setDescription(buildingDTO.getDescription());
        building.setQuarter(buildingDTO.getQuarter());
        building.setDistrict(buildingDTO.getDistrict());
        building.setCoordinates(createPolygon(buildingDTO.getCoordinates()));
        return building;
    }

    private Building GeoJsonToBuilding(Feature geoJsonFeature) {
        Building building = new Building();
        building.setName((String) geoJsonFeature.getProperties().get("name"));
        building.setAddress((String) geoJsonFeature.getProperties().get("address"));
        building.setDescription((String) geoJsonFeature.getProperties().get("description"));
        building.setQuarter((String) geoJsonFeature.getProperties().get("quarter"));
        building.setDistrict((String) geoJsonFeature.getProperties().get("district"));
        List<List<List<Double>>> geometry_coords = (List<List<List<Double>>>) geoJsonFeature.getGeometry().get("coordinates");
        List<List<Double>> coordinates = geometry_coords.getFirst();
        building.setCoordinates(createPolygon(coordinates));
        return building;
    }

    private BuildingDTO BuildingToDTO(Building building) {
        BuildingDTO buildingDTO = new BuildingDTO();
        buildingDTO.setId(building.getId());
        buildingDTO.setName(building.getName());
        buildingDTO.setAddress(building.getAddress());
        buildingDTO.setDescription(building.getDescription());
        buildingDTO.setQuarter(building.getQuarter());
        buildingDTO.setDistrict(building.getDistrict());
        buildingDTO.setCoordinates(convertPolygon(building.getCoordinates()));
        return buildingDTO;
    }

    private Feature BuildingToGeoJson(Building building) {
        Feature feature = new Feature();
        feature.getProperties().put("id", building.getId());
        feature.getProperties().put("name", building.getName());
        feature.getProperties().put("address", building.getAddress());
        feature.getProperties().put("description", building.getDescription());
        feature.getProperties().put("quarter", building.getQuarter());
        feature.getProperties().put("district", building.getDistrict());

        feature.getGeometry().put("type", "Polygon");
        List<List<List<Double>>> coordinates = new ArrayList<>();
        coordinates.add(convertPolygon(building.getCoordinates()));
        feature.getGeometry().put("coordinates", coordinates);
        return feature;
    }
}
