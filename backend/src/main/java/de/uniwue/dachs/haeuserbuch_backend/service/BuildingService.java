package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.*;
import de.uniwue.dachs.haeuserbuch_backend.model.Address;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.model.BuildingName;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchIndexAffecting;
import de.uniwue.dachs.haeuserbuch_backend.specification.BuildingSpecifications;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private final BuildingNameMapper buildingNameMapper;
    private final AddressMapper addressMapper;

    public BuildingService(BuildingRepository buildingRepository,
                           BuildingMapper buildingMapper, SourceMapper sourceMapper, DistrictMapper districtMapper, QuarterMapper quarterMapper, BuildingNameMapper buildingNameMapper, AddressMapper addressMapper) {
        this.buildingRepository = buildingRepository;
        this.buildingMapper = buildingMapper;
        this.sourceMapper = sourceMapper;
        this.districtMapper = districtMapper;
        this.quarterMapper = quarterMapper;
        this.buildingNameMapper = buildingNameMapper;
        this.addressMapper = addressMapper;
    }

    /**
     * GET all buildings
     * @return {@link FeatureCollection} containing all buildings as features or an empty {@link List} if no buildings are found
     */
    @Cacheable("buildings")
    public FeatureCollection getAllBuildings() {
        FeatureCollection featureCollection = new FeatureCollection();
        List<Building> buildings = buildingRepository.findAll();
        featureCollection.setFeatures(buildingMapper.BuildingsToFeatures(buildings));
        return featureCollection;
    }

    /**
     * GET building by ID
     * @param id of the building
     * @return {@link Optional} of {@link Feature} representing the building or an empty {@link Optional} if no building with the given ID is found
     */
    @Cacheable(value = "buildings", key = "#id")
    public Optional<Feature> getBuildingById(Long id) {
        return buildingRepository.findById(id).map(buildingMapper::BuildingToFeature);
    }

    /**
     * GET search buildings by district, quarter, or source
     * @param districtId ID of the district
     * @param districtName Name of the district
     * @param quarterId ID of the quarter
     * @param quarterName Name of the quarter
     * @param sourceId ID of the source
     * @param sourceName Name of the source
     * @param streetId ID of the street
     * @param streetName Name of the street
     * @return {@link List} of {@link BuildingDTO} matching the search criteria or an empty {@link List} if no buildings match the criteria
     */
    public List<BuildingDTO> searchBuildings(
            Long districtId,
            String districtName,
            Long quarterId,
            String quarterName,
            Long sourceId,
            String sourceName,
            Long streetId,
            String streetName
    ) {
        Specification<Building> spec = Specification.where(null);

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
        if (sourceId != null) {
            spec = spec.and(BuildingSpecifications.hasSourceId(sourceId));
        }
        if (sourceName != null && !sourceName.isEmpty()) {
            spec = spec.and(BuildingSpecifications.hasSourceName(sourceName));
        }
        if (streetId != null) {
            spec = spec.and(BuildingSpecifications.hasStreetId(streetId));
        }
        if (streetName != null && !streetName.isEmpty()) {
            spec = spec.and(BuildingSpecifications.hasAddressStreetName(streetName));
        }
        List<Building> response = buildingRepository.findAll(spec);
        return buildingMapper.buildingsToDTOs(response);
    }

    /**
     * POST create a new building
     * @param feature is a {@link Feature} object representing the building to create
     * @return the created building as a {@link Feature} object or throws a {@link RuntimeException} if the creation fails due to invalid input data
     */
    @Transactional
    @CacheEvict(value = "buildings", allEntries = true)
    @SearchIndexAffecting
    public Feature createBuilding(Feature feature) {
        Building building = buildingMapper.FeatureToBuilding(feature);
        Building savedBuilding = buildingRepository.save(building);
        return buildingMapper.BuildingToFeature(savedBuilding);
    }

    /**
     * PUT update an existing building
     * @param id of the building to update
     * @param updatedFeature is a {@link Feature} object containing the updated data for the building
     * @return the updated building as a {@link Feature} object or throws a {@link NoSuchElementException} if no building with the given ID exists or a {@link IllegalArgumentException} if the update fails due to invalid input data
     */
    @Transactional
    @CacheEvict(value = "buildings", allEntries = true)
    @SearchIndexAffecting
    public Feature updateBuilding(Long id, Feature updatedFeature) {
        return buildingRepository.findById(id).map(entity -> {
            Building mappedBuilding = buildingMapper.FeatureToBuilding(updatedFeature);
            BuildingProperties properties = (BuildingProperties) updatedFeature.getProperties();
            if (properties != null) {
                entity.setYear(properties.getYear());
                entity.setParcelNumber(properties.getParcelNumber());
                entity.setParcelNumberCounter(properties.getParcelNumberCounter());
                Set<BuildingName> newNames = buildingNameMapper.BuildingNameDTOsToBuildingNames(properties.getNames());
                entity.getNames().clear();
                entity.getNames().addAll(newNames);
                Set<Address> newAddresses = addressMapper.AddressDTOsToAddresses(properties.getAddresses());
                entity.getAddresses().clear();
                entity.getAddresses().addAll(newAddresses);
                entity.setPartType(properties.getPartType());
                entity.setObject(properties.getObject());
                entity.setQuarter(quarterMapper.QuarterDTOToQuarter(properties.getQuarter()));
                entity.setDistrict(districtMapper.DistrictDTOToDistrict(properties.getDistrict()));
                entity.setPropertyNumber(properties.getPropertyNumber());
                entity.setDistrictPropertyNumber(properties.getDistrictPropertyNumber());
                entity.setSources(properties.getSources() != null
                        ? sourceMapper.SourceDTOsToSources(properties.getSources())
                        : new HashSet<>());
                entity.setLiterature(properties.getLiterature() != null
                        ? sourceMapper.SourceDTOsToSources(properties.getLiterature())
                        : new HashSet<>());
                entity.setFiles(mappedBuilding.getFiles());
                entity.setInternalNotes(properties.getInternalNotes());
                entity.setGeneralNotes(properties.getGeneralNotes());
            }
            entity.setCoordinates(mappedBuilding.getCoordinates());
            Building updatedEntity = buildingRepository.save(entity);
            return buildingMapper.BuildingToFeature(updatedEntity);
        }).orElseThrow(() -> new NoSuchElementException("Building with ID " + id + " does not exist"));
    }

    /**
     * DELETE a building by ID
     * @param id of the building to delete
     * @throws RuntimeException if no building with the given ID exists
     */
    @Transactional
    @CacheEvict(value = "buildings", allEntries = true)
    @SearchIndexAffecting
    public void deleteBuilding(Long id) {
        if (!buildingRepository.existsById(id)) {
            throw new NoSuchElementException("Building with id '" + id + "' does not exist");
        }
        buildingRepository.deleteById(id);
    }

    /**
     * Allows searching for buildings based on a search term.
     * @param query Search term.
     * @return A {@link List} of {@link BuildingDTO} matching the search term or an empty {@link List} if no buildings match the search term.
     */
    public List<BuildingDTO> searchBuildings(@Param("query") String query) {
        return buildingRepository.searchBuildings(query).stream()
                .map(buildingMapper::buildingToDTO)
                .filter(Objects::nonNull)
                .toList();
    }
}
