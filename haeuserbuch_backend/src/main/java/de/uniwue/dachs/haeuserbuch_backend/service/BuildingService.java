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
        building.setHouse_number(buildingDTO.getHouse_number());
        building.setPart_type(buildingDTO.getPart_type());
        building.setSpecial_status(buildingDTO.getSpecial_status());
        building.setQuarter(buildingDTO.getQuarter());
        building.setDistrict(buildingDTO.getDistrict());
        building.setDistrict_house_number(buildingDTO.getDistrict_house_number());
        building.setSource(buildingDTO.getSource());
        building.setNote(buildingDTO.getNote());
        if (buildingDTO.getCoordinates() != null) {
            building.setCoordinates(createPolygon(buildingDTO.getCoordinates()));
        }
        return building;
    }

    private Building GeoJsonToBuilding(Feature feature) {
        Building building = new Building();
        building.setName((String) feature.getProperties().get("name"));
        building.setHouse_number((String) feature.getProperties().get("house_number"));
        building.setPart_type((String) feature.getProperties().get("part_type"));
        building.setSpecial_status((String) feature.getProperties().get("special_status"));
        building.setQuarter((String) feature.getProperties().get("quarter"));
        building.setDistrict((String) feature.getProperties().get("district"));
        building.setDistrict_house_number((String) feature.getProperties().get("district_house_number"));
        building.setSource((String) feature.getProperties().get("source"));
        building.setNote((String) feature.getProperties().get("note"));
        if (feature.getGeometry() != null) {
            if (feature.getGeometry() instanceof PolygonGeometry geometry) {
                List<List<List<Double>>> geometry_coords = geometry.getCoordinates();
                if (geometry_coords.isEmpty()) {
                    throw new IllegalArgumentException("Invalid coordinates");
                }
                building.setCoordinates(createPolygon(geometry_coords));
            } else {
                throw new IllegalArgumentException("Unsupported geometry type");
            }
        }
        return building;
    }

    private BuildingDTO BuildingToDTO(Building building) {
        BuildingDTO buildingDTO = new BuildingDTO();
        buildingDTO.setId(building.getId());
        buildingDTO.setName(building.getName());
        buildingDTO.setHouse_number(building.getHouse_number());
        buildingDTO.setPart_type(building.getPart_type());
        buildingDTO.setSpecial_status(building.getSpecial_status());
        buildingDTO.setQuarter(building.getQuarter());
        buildingDTO.setDistrict(building.getDistrict());
        buildingDTO.setDistrict_house_number(building.getDistrict_house_number());
        buildingDTO.setSource(building.getSource());
        buildingDTO.setNote(building.getNote());
        if (building.getCoordinates() != null) {
            buildingDTO.setCoordinates(convertPolygon(building.getCoordinates()));
        }
        return buildingDTO;
    }

    private Feature BuildingToGeoJson(Building building) {
        Feature feature = new Feature();
        feature.getProperties().put("id", building.getId());
        feature.getProperties().put("name", building.getName());
        feature.getProperties().put("house_number", building.getHouse_number());
        feature.getProperties().put("part_type", building.getPart_type());
        feature.getProperties().put("special_status", building.getSpecial_status());
        feature.getProperties().put("quarter", building.getQuarter());
        feature.getProperties().put("district", building.getDistrict());
        feature.getProperties().put("district_house_number", building.getDistrict_house_number());
        feature.getProperties().put("source", building.getSource());
        feature.getProperties().put("note", building.getNote());
        if (building.getCoordinates() != null) {
            PolygonGeometry geometry = new PolygonGeometry();
            List<List<List<Double>>> coordinates = convertPolygon(building.getCoordinates());
            geometry.setCoordinates(coordinates);
            feature.setGeometry(geometry);
        }
        return feature;
    }
}
