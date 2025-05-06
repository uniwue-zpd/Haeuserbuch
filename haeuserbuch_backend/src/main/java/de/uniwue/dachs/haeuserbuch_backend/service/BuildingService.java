package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.FeatureCollection;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.PolygonGeometry;
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

    // Get building by its ID
    public Optional<BuildingDTO> getBuildingById(Long id) {
        return buildingRepository.findById(id).map(this::BuildingToDTO);
    }

    // Get building by its ID (GeoJSON)
    public Optional<Feature> getBuildingFeatureById(Long id) {
        return buildingRepository.findById(id).map(this::BuildingToGeoJson);
    }

    // Create new building
    @Transactional
    public void createBuilding(BuildingDTO buildingDTO) {
        Building building = DtoToBuilding(buildingDTO);
        buildingRepository.save(building);
    }

    // Create new building from GeoJSON
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

    private Building GeoJsonToBuilding(Feature feature) {
        if (!(feature.getGeometry() instanceof PolygonGeometry geometry)) {
            throw new IllegalArgumentException("Unsupported geometry type");
        }
        List<List<List<Double>>> geometry_coords = geometry.getCoordinates();
        if (geometry_coords.size() != 1 || geometry_coords.getFirst().size() < 3) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
        List<List<Double>> coordinates = geometry_coords.getFirst();
        Building building = new Building();
        building.setName((String) feature.getProperties().get("name"));
        building.setAddress((String) feature.getProperties().get("address"));
        building.setDescription((String) feature.getProperties().get("description"));
        building.setQuarter((String) feature.getProperties().get("quarter"));
        building.setDistrict((String) feature.getProperties().get("district"));
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
        PolygonGeometry geometry = new PolygonGeometry();
        List<List<List<Double>>> coordinates = new ArrayList<>();
        coordinates.add(convertPolygon(building.getCoordinates()));
        geometry.setCoordinates(coordinates);
        feature.setGeometry(geometry);
        return feature;
    }
}
