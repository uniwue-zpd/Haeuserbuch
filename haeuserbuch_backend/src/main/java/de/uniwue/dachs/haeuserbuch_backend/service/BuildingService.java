package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.*;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper.*;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPoint;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPolygon;

@Service
public class BuildingService {
    private final BuildingRepository buildingRepository;

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    // GET all buildings as feature collection
    public FeatureCollection getAllBuildings() {
        List<Building> buildings = buildingRepository.findAll();
        FeatureCollection featureCollection = new FeatureCollection();
        List<Feature> features = new ArrayList<>();
        for (Building building : buildings) {
            Feature feature = BuildingToFeature(building);
            features.add(feature);
        }
        featureCollection.setFeatures(features);
        return featureCollection;
    }

    // GET building by its ID
    public Optional<Feature> getBuildingById(Long id) {
        return buildingRepository.findById(id).map(BuildingMapper::BuildingToFeature);
    }

    // POST Create new building
    @Transactional
    public void createBuilding(Feature feature) {
        Building building = FeatureToBuilding(feature);
        buildingRepository.save(building);
    }

    // PUT Update existing building
    @Transactional
    public void updateBuilding(Long id, Feature updatedFeature) {
        buildingRepository.findById(id).map(entity -> {
            BuildingProperties properties = (BuildingProperties) updatedFeature.getProperties();
            entity.setName(properties != null ? properties.getName() : null);
            entity.setHouse_number(properties != null ? properties.getHouse_number() : null);
            entity.setPart_type(properties != null ? properties.getPart_type() : null);
            entity.setSpecial_status(properties != null ? properties.getSpecial_status() : null);
            entity.setQuarter(properties != null ? properties.getQuarter() : null);
            entity.setDistrict(properties != null ? properties.getDistrict() : null);
            entity.setDistrict_house_number(properties != null ? properties.getDistrict_house_number() : null);
            entity.setPrimary_source(properties != null ? properties.getPrimary_source() : null);
            entity.setNotes(properties != null ? properties.getNotes() : null);
            if (updatedFeature.getGeometry() != null) {
                if (updatedFeature.getGeometry() instanceof PointGeometry pointGeometry) {
                    entity.setCoordinates(createPoint(pointGeometry.getCoordinates()));
                } else if (updatedFeature.getGeometry() instanceof PolygonGeometry polygonGeometry) {
                    entity.setCoordinates(createPolygon(polygonGeometry.getCoordinates()));
                } else {
                    throw new IllegalArgumentException("Unsupported geometry type");
                }
            }
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
