package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.FeatureCollection;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper.DTOToBuilding;
import static de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper.BuildingToDTO;
import static de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper.BuildingToGeoJson;
import static de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper.GeoJsonToBuilding;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPolygon;

@Service
public class BuildingService {
    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    // GET all buildings
    public List<BuildingDTO> getAllBuildings() {
        List<Building> buildings = buildingRepository.findAll();
        List<BuildingDTO> buildingDTOs = new ArrayList<>();
        for (Building building : buildings) {
            BuildingDTO current_dto = BuildingToDTO(building);
            buildingDTOs.add(current_dto);
        }
        return buildingDTOs;
    }

    // GET all buildings as feature collection
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

    // GET building by its ID
    public Optional<BuildingDTO> getBuildingById(Long id) {
        return buildingRepository.findById(id).map(BuildingMapper::BuildingToDTO);
    }

    // GET building by its ID (GeoJSON)
    public Optional<Feature> getBuildingFeatureById(Long id) {
        return buildingRepository.findById(id).map(BuildingMapper::BuildingToGeoJson);
    }

    // POST Create new building
    @Transactional
    public void createBuilding(BuildingDTO buildingDTO) {
        Building building = DTOToBuilding(buildingDTO);
        buildingRepository.save(building);
    }

    // POST Create new building from GeoJSON
    @Transactional
    public void createBuildingFromGeoJSON(Feature feature) {
        Building building = GeoJsonToBuilding(feature);
        buildingRepository.save(building);
    }

    // PUT Update existing building
    @Transactional
    public void updateBuilding(Long id, BuildingDTO updatedBuildingDTO) {
        buildingRepository.findById(id).map(entity -> {
            entity.setName(updatedBuildingDTO.getName());
            entity.setHouse_number(updatedBuildingDTO.getHouse_number());
            entity.setPart_type(updatedBuildingDTO.getPart_type());
            entity.setSpecial_status(updatedBuildingDTO.getSpecial_status());
            entity.setQuarter(updatedBuildingDTO.getQuarter());
            entity.setDistrict(updatedBuildingDTO.getDistrict());
            entity.setDistrict_house_number(updatedBuildingDTO.getDistrict_house_number());
            entity.setSource(updatedBuildingDTO.getSource());
            entity.setNote(updatedBuildingDTO.getNote());
            entity.setCoordinates((updatedBuildingDTO.getCoordinates() != null)
                    ? createPolygon(updatedBuildingDTO.getCoordinates())
                    : null);
            return buildingRepository.save(entity);
        }).orElseThrow(() -> new NoSuchElementException("Building with ID " + id + " does not exist"));
    }

    // PUT Update existing building from GeoJSON
    @Transactional
    public void updateBuildingFromGeoJSON(Long id, Feature updatedFeature) {
        Building updatedBuilding = GeoJsonToBuilding(updatedFeature);
        buildingRepository.findById(id).map(entity -> {
            entity.setName(updatedBuilding.getName());
            entity.setHouse_number(updatedBuilding.getHouse_number());
            entity.setPart_type(updatedBuilding.getPart_type());
            entity.setSpecial_status(updatedBuilding.getSpecial_status());
            entity.setQuarter(updatedBuilding.getQuarter());
            entity.setDistrict(updatedBuilding.getDistrict());
            entity.setDistrict_house_number(updatedBuilding.getDistrict_house_number());
            entity.setSource(updatedBuilding.getSource());
            entity.setNote(updatedBuilding.getNote());
            entity.setCoordinates(updatedBuilding.getCoordinates());
            return buildingRepository.save(entity);
        }).orElseThrow(() -> new NoSuchElementException("Building with ID " + id + " does not exist"));
    }

    // DELETE building by ID
    @Transactional
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new RuntimeException("Building with id '" + id + "' does not exist");
        }
        buildingRepository.deleteById(id);
    }
}
