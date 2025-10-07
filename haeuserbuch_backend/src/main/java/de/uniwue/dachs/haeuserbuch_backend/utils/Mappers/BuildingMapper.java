package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.*;
import de.uniwue.dachs.haeuserbuch_backend.embeddables.BuildingName;
import de.uniwue.dachs.haeuserbuch_backend.model.*;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.BuildingProperties;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PointGeometry;
import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.PolygonGeometry;
import de.uniwue.dachs.haeuserbuch_backend.repository.DistrictRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.QuarterRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.StreetRepository;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.*;

@Component
public class BuildingMapper {
    private final SourceRepository sourceRepository;
    private final QuarterRepository quarterRepository;
    private final DistrictRepository districtRepository;
    private final StreetRepository streetRepository;

    public BuildingMapper(SourceRepository sourceRepository, QuarterRepository quarterRepository, DistrictRepository districtRepository, StreetRepository streetRepository) {
        this.sourceRepository = sourceRepository;
        this.quarterRepository = quarterRepository;
        this.districtRepository = districtRepository;
        this.streetRepository = streetRepository;
    }

    public Feature BuildingToFeature(Building building) {
        Feature feature = new Feature();
        BuildingProperties properties = new BuildingProperties();
        feature.setId(building.getId());
        properties.setNames(getBuildingNamesDTOs(building.getNames()));
        properties.setHouseNumber(building.getHouseNumber());
        properties.setCurrentHouseNumber(building.getCurrentHouseNumber());
        properties.setPartType(building.getPartType());
        properties.setSpecialStatus(building.getSpecialStatus());
        properties.setCurrentStreet(getStreetDTO(building.getCurrentStreet()));
        properties.setQuarter(getQuarterDTO(building.getQuarter()));
        properties.setDistrict(getDistrictDTO(building.getDistrict()));
        properties.setDistrictHouseNumber(building.getDistrictHouseNumber());
        properties.setPrimarySources(getSourceDTOs(building.getPrimarySources()));
        properties.setSecondarySources(getSourceDTOs(building.getSecondarySources()));
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
                building.setNames(getBuildingNames(properties.getNames()));
                building.setHouseNumber(properties.getHouseNumber());
                building.setCurrentHouseNumber(properties.getCurrentHouseNumber());
                building.setPartType(properties.getPartType());
                building.setSpecialStatus(properties.getSpecialStatus());
                building.setCurrentStreet(getStreet(properties.getCurrentStreet()));
                building.setQuarter(getQuarter(properties.getQuarter()));
                building.setDistrict(getDistrict(properties.getDistrict()));
                building.setDistrictHouseNumber(properties.getDistrictHouseNumber());
                building.setPrimarySources(getSources(properties.getPrimarySources()));
                building.setSecondarySources(getSources(properties.getSecondarySources()));
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

    public Set<SourceDTO> getSourceDTOs(Set<Source> sources) {
        if (sources == null || sources.isEmpty()) {
            return new HashSet<>();
        }
        Set<SourceDTO> sourceDTOs = new HashSet<>();
        for (Source source: sources) {
            if (source != null) {
                SourceDTO sourceDTO = new SourceDTO();
                sourceDTO.setId(source.getId());
                sourceDTO.setTitle(source.getTitle());
                sourceDTOs.add(sourceDTO);
            }
        }
        return sourceDTOs;
    }

    public Set<Source> getSources(Set<SourceDTO> sourceDTOs) {
        if (sourceDTOs == null || sourceDTOs.isEmpty()) {
            return new HashSet<>();
        }
        return sourceDTOs.stream()
                .filter(Objects::nonNull)
                .map(dto -> sourceRepository.findById(dto.getId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public Quarter getQuarter(QuarterDTO quarterDTO) {
        if (quarterDTO == null || quarterDTO.getId() == null) {
            return null;
        }
        return quarterRepository.findById(quarterDTO.getId()).orElse(null);
    }

    public QuarterDTO getQuarterDTO(Quarter quarter) {
        if (quarter == null) {
            return null;
        }
        QuarterDTO quarterDTO = new QuarterDTO();
        quarterDTO.setId(quarter.getId());
        quarterDTO.setName(quarter.getName());
        return quarterDTO;
    }

    public District getDistrict(DistrictDTO districtDTO) {
        if (districtDTO == null || districtDTO.getId() == null) {
            return null;
        }
        return districtRepository.findById(districtDTO.getId()).orElse(null);
    }

    public DistrictDTO getDistrictDTO(District district) {
        if (district == null) {
            return null;
        }
        DistrictDTO districtDTO = new DistrictDTO();
        districtDTO.setId(district.getId());
        districtDTO.setName(district.getName());
        return districtDTO;
    }

    public Street getStreet(StreetDTO streetDTO) {
        if (streetDTO == null || streetDTO.getId() == null) {
            return null;
        }
        return streetRepository.findById(streetDTO.getId()).orElse(null);
    }

    public StreetDTO getStreetDTO(Street street) {
        if (street == null) {
            return null;
        }
        StreetDTO streetDTO = new StreetDTO();
        streetDTO.setId(street.getId());
        streetDTO.setName(street.getName());
        return streetDTO;
    }

    public Set<BuildingNameDTO> getBuildingNamesDTOs(Set<BuildingName> names) {
        if (names == null || names.isEmpty()) {
            return new HashSet<>();
        }
        return names.stream().map(name -> {
            BuildingNameDTO dto = new BuildingNameDTO();
            dto.setName(name.getName());
            dto.setSource(name.getSource());
            return dto;
        }).collect(Collectors.toSet());
    }

    public Set<BuildingName> getBuildingNames(Set<BuildingNameDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return new HashSet<>();
        }
        return dtos.stream().map(dto -> {
            BuildingName name = new BuildingName();
            name.setName(dto.getName());
            name.setSource(dto.getSource());
            return name;
        }).collect(Collectors.toSet());
    }

}
