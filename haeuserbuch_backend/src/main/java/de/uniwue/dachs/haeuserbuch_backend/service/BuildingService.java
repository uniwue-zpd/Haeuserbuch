package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.*;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.BuildingMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPoint;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPolygon;

@Service
public class BuildingService {
    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    public BuildingService(BuildingRepository buildingRepository,
                           BuildingMapper buildingMapper) {
        this.buildingRepository = buildingRepository;
        this.buildingMapper = buildingMapper;
    }

    // GET all buildings as feature collection
    @Cacheable("buildings")
    public FeatureCollection getAllBuildings() {
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setFeatures(
                buildingRepository.findAll().stream()
                        .map(buildingMapper::BuildingToFeature)
                        .toList()
        );
        return featureCollection;
    }

    // GET building by its ID
    @Cacheable(value = "buildingById", key = "#id")
    public Optional<Feature> getBuildingById(Long id) {
        return buildingRepository.findById(id).map(buildingMapper::BuildingToFeature);
    }

    // POST Create new building
    @Transactional
    @CacheEvict(value = "buildings", allEntries = true)
    public void createBuilding(Feature feature) {
        Building building = buildingMapper.FeatureToBuilding(feature);
        buildingRepository.save(building);
    }

    // PUT Update existing building
    @Transactional
    @CachePut(value = "buildingById", key = "#id")
    public void updateBuilding(Long id, Feature updatedFeature) {
        buildingRepository.findById(id).map(entity -> {
            BuildingProperties properties = (BuildingProperties) updatedFeature.getProperties();
            entity.setName(properties != null ? properties.getName() : null);
            entity.setAltNames(properties != null ? properties.getAltNames() : new ArrayList<>());
            entity.setHouseNumber(properties != null ? properties.getHouseNumber() : null);
            entity.setPartType(properties != null ? properties.getPartType() : null);
            entity.setSpecialStatus(properties != null ? properties.getSpecialStatus() : null);
            entity.setQuarter(properties != null ? properties.getQuarter() : null);
            entity.setDistrict(properties != null ? properties.getDistrict() : null);
            entity.setDistrictHouseNumber(properties != null ? properties.getDistrictHouseNumber() : null);
            entity.setPrimarySources((properties != null && properties.getPrimarySources() != null)
                    ? buildingMapper.getOrSaveSources(properties.getPrimarySources())
                    : null);
            entity.setInternalNotes(properties != null ? properties.getInternalNotes() : null);
            entity.setGeneralNotes(properties != null ? properties.getGeneralNotes() : null);
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
    @CacheEvict(value = "buildingById", key = "#id")
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new RuntimeException("Building with id '" + id + "' does not exist");
        }
        buildingRepository.deleteById(id);
    }
}
