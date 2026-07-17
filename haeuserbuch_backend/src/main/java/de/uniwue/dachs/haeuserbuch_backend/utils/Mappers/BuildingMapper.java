package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.*;
import de.uniwue.dachs.haeuserbuch_backend.model.*;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Geometry;
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
        properties.setYear(building.getYear());
        properties.setParcelNumber(building.getParcelNumber());
        properties.setParcelNumberCounter(building.getParcelNumberCounter());
        properties.setNames(buildingNameMapper.BuildingNamesToDTOs(building.getNames()));
        properties.setPartType(building.getPartType());
        properties.setObject(building.getObject());
        properties.setAddresses(addressMapper.AddressesToDTOs(building.getAddresses()));
        properties.setQuarter(quarterMapper.QuarterToDTO(building.getQuarter()));
        properties.setDistrict(districtMapper.DistrictToDTO(building.getDistrict()));
        properties.setPropertyNumber(building.getPropertyNumber());
        properties.setDistrictPropertyNumber(building.getDistrictPropertyNumber());
        properties.setSources(sourceMapper.SourcesToDTOs(building.getSources()));
        properties.setLiterature(sourceMapper.SourcesToDTOs(building.getLiterature()));
        properties.setInternalNotes(building.getInternalNotes());
        properties.setGeneralNotes(building.getGeneralNotes());
        properties.setCreatedDate(building.getCreatedDate());
        properties.setCreatedBy(building.getCreatedBy());
        properties.setLastModifiedDate(building.getLastModifiedDate());
        properties.setLastModifiedBy(building.getLastModifiedBy());
        feature.setProperties(properties);
        Geometry geometry = building.getCoordinates();
        feature.setGeometry(geometryToDTO(geometry));
        return feature;
    }

    public List<Feature> BuildingsToFeatures(List<Building> buildings) {
        return buildings.stream()
                .sorted(Comparator.comparing(Building::getId))
                .map(this::BuildingToFeature)
                .filter(Objects::nonNull)
                .toList();
    }

    public Building FeatureToBuilding(Feature feature) {
        Building building = new Building();
        if (feature.getProperties() != null) {
            if (feature.getProperties() instanceof BuildingProperties properties) {
                building.setYear(properties.getYear());
                building.setParcelNumber(properties.getParcelNumber());
                building.setParcelNumberCounter(properties.getParcelNumberCounter());
                building.setNames(buildingNameMapper.BuildingNameDTOsToBuildingNames(properties.getNames()));
                building.setPartType(properties.getPartType());
                building.setObject(properties.getObject());
                building.setAddresses(addressMapper.AddressDTOsToAddresses(properties.getAddresses()));
                building.setQuarter(quarterMapper.QuarterDTOToQuarter(properties.getQuarter()));
                building.setDistrict(districtMapper.DistrictDTOToDistrict(properties.getDistrict()));
                building.setPropertyNumber(properties.getPropertyNumber());
                building.setDistrictPropertyNumber(properties.getDistrictPropertyNumber());
                building.setSources(sourceMapper.SourceDTOsToSources(properties.getSources()));
                building.setLiterature(sourceMapper.SourceDTOsToSources(properties.getLiterature()));
                building.setInternalNotes(properties.getInternalNotes());
                building.setGeneralNotes(properties.getGeneralNotes());
            } else {
                throw new IllegalArgumentException("Unsupported properties type");
            }
        }
        Geometry geometry = DTOToGeometry(feature.getGeometry());
        building.setCoordinates(geometry);
        return building;
    }

    public Building DTOToBuilding(BuildingDTO buildingDTO) {
        if (buildingDTO == null || buildingDTO.getId() == null) {
            return null;
        }
        return buildingRepository.findById(buildingDTO.getId()).orElse(null);
    }

    public BuildingDTO buildingToDTO(Building building) {
        if (building == null) return null;
        BuildingDTO buildingDTO = new BuildingDTO();
        buildingDTO.setId(building.getId());
        buildingDTO.setDistrictPropertyNumber(building.getDistrictPropertyNumber());
        return buildingDTO;
    }

    public List<BuildingDTO> buildingsToDTOs(List<Building> buildings) {
        return buildings.stream()
                .map(this::buildingToDTO)
                .sorted(Comparator.comparing(BuildingDTO::getId))
                .toList();
    }

    private de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Geometry geometryToDTO(Geometry geometry) {
        if (geometry == null) return null;
        return switch(geometry) {
            case Point point -> {
                PointGeometry pointGeometry = new PointGeometry();
                pointGeometry.setCoordinates(convertPoint(point));
                yield pointGeometry;
            }
            case Polygon polygon -> {
                PolygonGeometry polygonGeometry = new PolygonGeometry();
                polygonGeometry.setCoordinates(convertPolygon(polygon));
                yield polygonGeometry;
            }
            case MultiPolygon multiPolygon -> {
                MultiPolygonGeometry multiPolygonGeometry = new MultiPolygonGeometry();
                multiPolygonGeometry.setCoordinates(convertMultiPolygon(multiPolygon));
                yield multiPolygonGeometry;
            }
            case LineString lineString -> {
                LineStringGeometry lineStringGeometry = new LineStringGeometry();
                lineStringGeometry.setCoordinates(convertLineString(lineString));
                yield lineStringGeometry;
            }
            default -> throw new IllegalArgumentException("Unsupported geometry type" + geometry.getGeometryType());
        };
    }

    public Geometry DTOToGeometry(de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Geometry geometry) {
        if (geometry == null) return null;
        return switch (geometry) {
            case PointGeometry pointGeometry -> createPoint(pointGeometry.getCoordinates());
            case PolygonGeometry polygonGeometry -> createPolygon(polygonGeometry.getCoordinates());
            case LineStringGeometry lineStringGeometry -> createLineString(lineStringGeometry.getCoordinates());
            case MultiPolygonGeometry multiPolygonGeometry -> createMultiPolygon(multiPolygonGeometry.getCoordinates());
            default -> throw new IllegalArgumentException("Unsupported geometry type" + geometry.getClass().getSimpleName());
        };
    }
}
