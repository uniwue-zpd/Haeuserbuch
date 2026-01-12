package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.*;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.BuildingProperties;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PointGeometry;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PolygonGeometry;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.*;

@Component
public class BuildingMapper {
    private final SourceMapper sourceMapper;
    private final DistrictMapper districtMapper;
    private final QuarterMapper quarterMapper;
    private final BuildingNameMapper buildingNameMapper;
    private final BuildingRepository buildingRepository;
    private final AddressMapper addressMapper;

    public BuildingMapper(SourceMapper sourceMapper, DistrictMapper districtMapper, QuarterMapper quarterMapper, BuildingNameMapper buildingNameMapper, BuildingRepository buildingRepository, AddressMapper addressMapper) {
        this.sourceMapper = sourceMapper;
        this.districtMapper = districtMapper;
        this.quarterMapper = quarterMapper;
        this.buildingNameMapper = buildingNameMapper;
        this.buildingRepository = buildingRepository;
        this.addressMapper = addressMapper;
    }

    public Feature BuildingToFeature(Building building) {
        Feature feature = new Feature();
        BuildingProperties properties = new BuildingProperties();
        feature.setId(building.getId());
        properties.setNames(buildingNameMapper.BuildingNamesToDTOs(building.getNames()));
        properties.setPartType(building.getPartType());
        properties.setSpecialStatus(building.getSpecialStatus());
        properties.setAddresses(addressMapper.AddressesToDTOs(building.getAddresses()));
        properties.setQuarter(quarterMapper.QuarterToDTO(building.getQuarter()));
        properties.setDistrict(districtMapper.DistrictToDTO(building.getDistrict()));
        properties.setHouseNumber(building.getHouseNumber());
        properties.setDistrictHouseNumber(building.getDistrictHouseNumber());
        properties.setPrimarySources(sourceMapper.SourcesToDTOs(building.getPrimarySources()));
        properties.setSecondarySources(sourceMapper.SourcesToDTOs(building.getSecondarySources()));
        properties.setInternalNotes(building.getInternalNotes());
        properties.setGeneralNotes(building.getGeneralNotes());
        properties.setCreatedDate(building.getCreatedDate());
        properties.setCreatedBy(building.getCreatedBy());
        properties.setLastModifiedDate(building.getLastModifiedDate());
        properties.setLastModifiedBy(building.getLastModifiedBy());
        feature.setProperties(properties);
        if (building.getCoordinates() != null) {
            Geometry geometry = building.getCoordinates();
            if (geometry instanceof Point) {
                PointGeometry pointGeometry = new PointGeometry();
                pointGeometry.setCoordinates(convertPoint(building.getCoordinates()));
                feature.setGeometry(pointGeometry);
            } else if (geometry instanceof Polygon) {
                PolygonGeometry polygonGeometry = new PolygonGeometry();
                List<List<List<Double>>> coordinates = convertPolygon(building.getCoordinates());
                polygonGeometry.setCoordinates(coordinates);
                feature.setGeometry(polygonGeometry);
            }
        }
        return feature;
    }

    public Building FeatureToBuilding(Feature feature) {
        Building building = new Building();
        if (feature.getProperties() != null) {
            if (feature.getProperties() instanceof BuildingProperties properties) {
                building.setNames(buildingNameMapper.BuildingNameDTOsToBuildingNames(properties.getNames()));
                building.setPartType(properties.getPartType());
                building.setSpecialStatus(properties.getSpecialStatus());
                building.setAddresses(addressMapper.AddressDTOsToAddresses(properties.getAddresses()));
                building.setQuarter(quarterMapper.QuarterDTOToQuarter(properties.getQuarter()));
                building.setDistrict(districtMapper.DistrictDTOToDistrict(properties.getDistrict()));
                building.setHouseNumber(properties.getHouseNumber());
                building.setDistrictHouseNumber(properties.getDistrictHouseNumber());
                building.setPrimarySources(sourceMapper.SourceDTOsToSources(properties.getPrimarySources()));
                building.setSecondarySources(sourceMapper.SourceDTOsToSources(properties.getSecondarySources()));
                building.setInternalNotes(properties.getInternalNotes());
                building.setGeneralNotes(properties.getGeneralNotes());
            } else {
                throw new IllegalArgumentException("Unsupported properties type");
            }
        }
        if (feature.getGeometry() != null) {
            if (feature.getGeometry() instanceof PointGeometry) {
                List<Double> coordinates = ((PointGeometry) feature.getGeometry()).getCoordinates();
                building.setCoordinates(createPoint(coordinates));
            } else if (feature.getGeometry() instanceof PolygonGeometry) {
                List<List<List<Double>>> coordinates = ((PolygonGeometry) feature.getGeometry()).getCoordinates();
                building.setCoordinates(createPolygon(coordinates));
            } else {
                throw new IllegalArgumentException("Unsupported geometry type");
            }
        }
        return building;
    }

    public Building buildingDTOToBuilding(BuildingDTO buildingDTO) {
        if (buildingDTO == null || buildingDTO.getId() == null) {
            return null;
        }
        return buildingRepository.findById(buildingDTO.getId()).orElse(null);
    }

    public List<Building> buildingDTOsToBuildings(List<BuildingDTO> buildingDTOs) {
        return buildingDTOs.stream().map(this::buildingDTOToBuilding).filter(Objects::nonNull).toList();
    }

    public BuildingDTO buildingToBuildingDTO(Building building) {
        BuildingDTO buildingDTO = new BuildingDTO();
        buildingDTO.setId(building.getId());
        buildingDTO.setDistrictHouseNumber(building.getDistrictHouseNumber());
        return buildingDTO;
    }

    public List<BuildingDTO> buildingsToBuildingDTOs(List<Building> buildings) {
        return buildings.stream()
                .map(this::buildingToBuildingDTO)
                .sorted(Comparator.comparing(BuildingDTO::getId))
                .toList();
    }
}
