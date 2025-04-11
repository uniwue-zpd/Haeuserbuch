package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BuildingService {
    private final BuildingRepository buildingRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public List<BuildingDTO> getAllBuildings() {
        List<Building> buildings = buildingRepository.findAll();
        List<BuildingDTO> buildingDTOs = new ArrayList<>();
        for (Building building : buildings) {
            BuildingDTO current_dto = new BuildingDTO();
            current_dto.setId(building.getId());
            current_dto.setName(building.getName());
            current_dto.setAddress(building.getAddress());
            current_dto.setDescription(building.getDescription());
            current_dto.setShape(convertPolygon(building.getShape()));
            buildingDTOs.add(current_dto);
        }
        return buildingDTOs;
    }

    public Optional<BuildingDTO> getBuildingById(Long id) {
        return buildingRepository.findById(id).map(entity -> {
            BuildingDTO current_dto = new BuildingDTO();
            current_dto.setId(entity.getId());
            current_dto.setName(entity.getName());
            current_dto.setAddress(entity.getAddress());
            current_dto.setDescription(entity.getDescription());
            current_dto.setShape(convertPolygon(entity.getShape()));
            return current_dto;
        });
    }

    @Transactional
    public Building createBuilding(BuildingDTO buildingDTO) {
        Building building = new Building();
        building.setName(buildingDTO.getName());
        building.setAddress(buildingDTO.getAddress());
        building.setDescription(buildingDTO.getDescription());
        building.setShape(createPolygon(buildingDTO.getShape()));
        return buildingRepository.save(building);
    }

    // Converts the shape to a polygon
    public Polygon createPolygon(Double[][] polygon) {
        Coordinate[] coordinates = new Coordinate[polygon.length];
        for (int i = 0; i < polygon.length; i++) {
            coordinates[i] = new Coordinate(polygon[i][0], polygon[i][1]);
        }
        LinearRing ring = geometryFactory.createLinearRing(coordinates);
        return geometryFactory.createPolygon(ring, null);
    }

    // Converts the polygon to a shape
    public Double[][] convertPolygon(Polygon polygon) {
        Coordinate[] coordinates = polygon.getCoordinates();
        Double[][] polygon_array = new Double[coordinates.length][2];
        for (int i = 0; i < coordinates.length; i++) {
            polygon_array[i][0] = coordinates[i].getX();
            polygon_array[i][1] = coordinates[i].getY();
        }
        return polygon_array;
    }
}
