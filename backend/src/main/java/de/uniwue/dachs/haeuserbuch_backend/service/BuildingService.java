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
    private final FileMapper fileMapper;

    public BuildingService(BuildingRepository buildingRepository,
                           BuildingMapper buildingMapper, SourceMapper sourceMapper, DistrictMapper districtMapper, QuarterMapper quarterMapper, BuildingNameMapper buildingNameMapper, AddressMapper addressMapper, FileMapper fileMapper) {
        this.buildingRepository = buildingRepository;
        this.buildingMapper = buildingMapper;
        this.sourceMapper = sourceMapper;
        this.districtMapper = districtMapper;
        this.quarterMapper = quarterMapper;
        this.buildingNameMapper = buildingNameMapper;
        this.addressMapper = addressMapper;
        this.fileMapper = fileMapper;
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
     * GET buildings as GeoJSON, either all (cached) or filtered.
     * @param q free-text search term
     * @param districtId ID of the district
     * @param quarterId ID of the quarter
     * @param streetId ID of a street in the building's addresses
     * @param sourceId ID of a source or literature entry
     * @param georeferenced {@code true} only buildings with coordinates, {@code false} only without
     * @return {@link FeatureCollection} of the matching buildings (unfiltered result is cached)
     */
    @Cacheable(
            value = "buildings",
            key = "'all'",
            condition = "(#q == null || #q.isBlank()) && #districtId == null && #quarterId == null"
                    + " && #streetId == null && #sourceId == null && #georeferenced == null"
    )
    public FeatureCollection getBuildings(
            String q, Long districtId, Long quarterId, Long streetId, Long sourceId, Boolean georeferenced
    ) {
        FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setFeatures(buildingMapper.BuildingsToFeatures(
                findBuildings(q, districtId, quarterId, streetId, sourceId, georeferenced)
        ));
        return featureCollection;
    }

    /**
     * GET buildings as lightweight previews, either all or filtered.
     * @see #getBuildings(String, Long, Long, Long, Long, Boolean)
     * @return {@link List} of {@link BuildingDTO} matching the criteria or an empty {@link List}
     */
    public List<BuildingDTO> getBuildingPreviews(
            String q, Long districtId, Long quarterId, Long streetId, Long sourceId, Boolean georeferenced
    ) {
        return buildingMapper.buildingsToDTOs(
                findBuildings(q, districtId, quarterId, streetId, sourceId, georeferenced)
        );
    }

    private List<Building> findBuildings(
            String q, Long districtId, Long quarterId, Long streetId, Long sourceId, Boolean georeferenced
    ) {
        boolean unfiltered = (q == null || q.isBlank()) && districtId == null && quarterId == null
                && streetId == null && sourceId == null && georeferenced == null;
        Specification<Building> spec = Specification.where(null);

        if (q != null && !q.isBlank()) {
            spec = spec.and(BuildingSpecifications.matchesText(q));
        }
        if (districtId != null) {
            spec = spec.and(BuildingSpecifications.hasDistrictId(districtId));
        }
        if (quarterId != null) {
            spec = spec.and(BuildingSpecifications.hasQuarterId(quarterId));
        }
        if (streetId != null) {
            spec = spec.and(BuildingSpecifications.hasStreetId(streetId));
        }
        if (sourceId != null) {
            spec = spec.and(BuildingSpecifications.hasSourceId(sourceId));
        }
        if (georeferenced != null) {
            spec = spec.and(BuildingSpecifications.isGeoreferenced(georeferenced));
        }
        List<Building> buildings = new ArrayList<>(buildingRepository.findAll(spec));
        // Unfiltered: by ID. Filtered: by number/designation for readable result lists.
        buildings.sort(unfiltered ? Comparator.comparing(Building::getId) : BUILDING_ORDER);
        return buildings;
    }

    /** Buildings with a district property number first, then by designation (numbers compared numerically), then by ID. */
    private static final Comparator<Building> BUILDING_ORDER = Comparator
            .comparing((Building b) -> b.getDistrictPropertyNumber() == null || b.getDistrictPropertyNumber().isBlank())
            .thenComparing(BuildingService::designation, BuildingService::compareNatural)
            .thenComparing(Building::getId);

    private static String designation(Building building) {
        if (building.getDistrictPropertyNumber() != null && !building.getDistrictPropertyNumber().isBlank()) {
            return building.getDistrictPropertyNumber();
        }
        return building.getNames().stream()
                .map(BuildingName::getName)
                .filter(name -> name != null && !name.isBlank())
                .findFirst()
                .orElse(building.getObject() != null ? building.getObject() : "Gebäude " + building.getId());
    }

    /** Compares strings case-insensitively, treating runs of digits as numbers ("I 2" before "I 10"). */
    private static int compareNatural(String a, String b) {
        String[] partsA = a.toLowerCase().split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
        String[] partsB = b.toLowerCase().split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
        for (int i = 0; i < Math.min(partsA.length, partsB.length); i++) {
            String pa = partsA[i], pb = partsB[i];
            int result = pa.matches("\\d+") && pb.matches("\\d+")
                    ? new java.math.BigInteger(pa).compareTo(new java.math.BigInteger(pb))
                    : pa.compareTo(pb);
            if (result != 0) return result;
        }
        return Integer.compare(partsA.length, partsB.length);
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
            BuildingProperties properties = (BuildingProperties) updatedFeature.getProperties();
            if (properties != null) {
                entity.setYear(properties.getYear());
                entity.setParcelNumber(properties.getParcelNumber());
                entity.setParcelNumberCounter(properties.getParcelNumberCounter());
                buildingNameMapper.applyTo(entity.getNames(), properties.getNames());
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
                entity.setFiles(properties.getFiles() != null
                        ? fileMapper.toFiles(properties.getFiles())
                        : new HashSet<>());
                entity.setInternalNotes(properties.getInternalNotes());
                entity.setGeneralNotes(properties.getGeneralNotes());
            }
            entity.setCoordinates(buildingMapper.DTOToGeometry(updatedFeature.getGeometry()));
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
