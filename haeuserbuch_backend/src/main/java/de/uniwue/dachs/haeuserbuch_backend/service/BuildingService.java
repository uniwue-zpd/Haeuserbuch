package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.*;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.specification.BuildingSpecifications;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPoint;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPolygon;

/** Service class for managing Building entities.
 * Provides methods for CRUD operations and searching buildings based on various criteria.
 */
@Service
public class BuildingService {
    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;
    private final SourceMapper sourceMapper;
    private final DistrictMapper districtMapper;
    private final QuarterMapper quarterMapper;
    private final StreetMapper streetMapper;
    private final BuildingNameMapper buildingNameMapper;

    public BuildingService(BuildingRepository buildingRepository,
                           BuildingMapper buildingMapper, SourceMapper sourceMapper, DistrictMapper districtMapper, QuarterMapper quarterMapper, StreetMapper streetMapper, BuildingNameMapper buildingNameMapper) {
        this.buildingRepository = buildingRepository;
        this.buildingMapper = buildingMapper;
        this.sourceMapper = sourceMapper;
        this.districtMapper = districtMapper;
        this.quarterMapper = quarterMapper;
        this.streetMapper = streetMapper;
        this.buildingNameMapper = buildingNameMapper;
    }

    // GET all buildings as feature collection
    @Cacheable("buildings")
    public FeatureCollection getAllBuildings() {
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setFeatures(
                buildingRepository.findAll().stream()
                        .map(buildingMapper::BuildingToFeature)
                        .sorted(Comparator.comparing(Feature::getId))
                        .toList()
        );
        return featureCollection;
    }

    // GET building by its ID
    @Cacheable(value = "buildings", key = "#id")
    public Optional<Feature> getBuildingById(Long id) {
        return buildingRepository.findById(id).map(buildingMapper::BuildingToFeature);
    }

    // GET buildings based on search criteria
    public List<BuildingDTO> searchBuildings(
            String name,
            Long districtId,
            String districtName,
            Long quarterId,
            String quarterName,
            Long streetId,
            String streetName,
            Long sourceId,
            String sourceName
    ) {
        Specification<Building> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(BuildingSpecifications.hasName(name));
        }
        if (districtId != null) {
            spec = spec.and(BuildingSpecifications.hasDistrictId(districtId));
        }
        if (districtName != null && !districtName.isEmpty()) {
            spec = spec.and(BuildingSpecifications.hasDistrict(districtName));
        }
        if (quarterId != null) {
            spec = spec.and(BuildingSpecifications.hasQuarterId(quarterId));
        }
        if (quarterName != null && !quarterName.isEmpty()) {
            spec = spec.and(BuildingSpecifications.hasQuarter(quarterName));
        }
        if (streetId != null) {
            spec = spec.and(BuildingSpecifications.hasStreetId(streetId));
        }
        if (streetName != null && !streetName.isEmpty()) {
            spec = spec.and(BuildingSpecifications.hasStreet(streetName));
        }
        if (sourceId != null) {
            spec = spec.and(BuildingSpecifications.hasSourceId(sourceId));
        }
        if (sourceName != null && !sourceName.isEmpty()) {
            spec = spec.and(BuildingSpecifications.hasSourceName(sourceName));
        }

        List<Building> response = buildingRepository.findAll(spec);
        return buildingMapper.buildingsToBuildingDTOs(response);
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
    @CacheEvict(value = "buildings", key = "#id")
    public void updateBuilding(Long id, Feature updatedFeature) {
        buildingRepository.findById(id).map(entity -> {
            BuildingProperties properties = (BuildingProperties) updatedFeature.getProperties();
            if (properties != null) {
                entity.setNames(properties.getNames() != null
                        ? buildingNameMapper.buildingNameDTOsToBuildingNames(properties.getNames())
                        : new HashSet<>());
                entity.setHouseNumber(properties.getHouseNumber());
                entity.setCurrentHouseNumber(properties.getCurrentHouseNumber());
                entity.setCurrentStreet(streetMapper.StreetDTOToStreet(properties.getCurrentStreet()));
                entity.setPartType(properties.getPartType());
                entity.setSpecialStatus(properties.getSpecialStatus());
                entity.setQuarter(quarterMapper.QuarterDTOToQuarter(properties.getQuarter()));
                entity.setDistrict(districtMapper.DistrictDTOToDistrict(properties.getDistrict()));
                entity.setDistrictHouseNumber(properties.getDistrictHouseNumber());
                entity.setPrimarySources(properties.getPrimarySources() != null
                        ? sourceMapper.SourceDTOsToSources(properties.getPrimarySources())
                        : new HashSet<>());
                entity.setSecondarySources(properties.getSecondarySources() != null
                        ? sourceMapper.SourceDTOsToSources(properties.getSecondarySources())
                        : new HashSet<>());
                entity.setInternalNotes(properties.getInternalNotes());
                entity.setGeneralNotes(properties.getGeneralNotes());
            }
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
    @CacheEvict(value = "buildings", allEntries = true)
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new RuntimeException("Building with id '" + id + "' does not exist");
        }
        buildingRepository.deleteById(id);
    }
}
