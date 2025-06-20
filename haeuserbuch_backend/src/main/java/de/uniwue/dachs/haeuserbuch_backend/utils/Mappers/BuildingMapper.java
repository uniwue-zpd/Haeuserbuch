package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.Feature;
import de.uniwue.dachs.haeuserbuch_backend.utils.GeoJSON.PolygonGeometry;

import java.util.List;

import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.convertPolygon;
import static de.uniwue.dachs.haeuserbuch_backend.utils.PostGIS.GeometryUtils.createPolygon;

public class BuildingMapper {
    public static BuildingDTO BuildingToDTO(Building building) {
        BuildingDTO buildingDTO = new BuildingDTO();
        buildingDTO.setId(building.getId());
        buildingDTO.setName(building.getName());
        buildingDTO.setHouse_number(building.getHouse_number());
        buildingDTO.setPart_type(building.getPart_type());
        buildingDTO.setSpecial_status(building.getSpecial_status());
        buildingDTO.setQuarter(building.getQuarter());
        buildingDTO.setDistrict(building.getDistrict());
        buildingDTO.setDistrict_house_number(building.getDistrict_house_number());
        buildingDTO.setSource(building.getSource());
        buildingDTO.setNote(building.getNote());
        if (building.getCoordinates() != null) {
            buildingDTO.setCoordinates(convertPolygon(building.getCoordinates()));
        }
        return buildingDTO;
    }

    public static Feature BuildingToGeoJson(Building building) {
        Feature feature = new Feature();
        feature.getProperties().put("id", building.getId());
        feature.getProperties().put("name", building.getName());
        feature.getProperties().put("house_number", building.getHouse_number());
        feature.getProperties().put("part_type", building.getPart_type());
        feature.getProperties().put("special_status", building.getSpecial_status());
        feature.getProperties().put("quarter", building.getQuarter());
        feature.getProperties().put("district", building.getDistrict());
        feature.getProperties().put("district_house_number", building.getDistrict_house_number());
        feature.getProperties().put("source", building.getSource());
        feature.getProperties().put("note", building.getNote());
        if (building.getCoordinates() != null) {
            PolygonGeometry geometry = new PolygonGeometry();
            List<List<List<Double>>> coordinates = convertPolygon(building.getCoordinates());
            geometry.setCoordinates(coordinates);
            feature.setGeometry(geometry);
        }
        return feature;
    }

    public static Building DTOToBuilding(BuildingDTO buildingDTO) {
        Building building = new Building();
        building.setName(buildingDTO.getName());
        building.setHouse_number(buildingDTO.getHouse_number());
        building.setPart_type(buildingDTO.getPart_type());
        building.setSpecial_status(buildingDTO.getSpecial_status());
        building.setQuarter(buildingDTO.getQuarter());
        building.setDistrict(buildingDTO.getDistrict());
        building.setDistrict_house_number(buildingDTO.getDistrict_house_number());
        building.setSource(buildingDTO.getSource());
        building.setNote(buildingDTO.getNote());
        if (buildingDTO.getCoordinates() != null) {
            building.setCoordinates(createPolygon(buildingDTO.getCoordinates()));
        }
        return building;
    }

    public static Building GeoJsonToBuilding(Feature feature) {
        Building building = new Building();
        building.setName((String) feature.getProperties().get("name"));
        building.setHouse_number((String) feature.getProperties().get("house_number"));
        building.setPart_type((String) feature.getProperties().get("part_type"));
        building.setSpecial_status((String) feature.getProperties().get("special_status"));
        building.setQuarter((String) feature.getProperties().get("quarter"));
        building.setDistrict((String) feature.getProperties().get("district"));
        building.setDistrict_house_number((String) feature.getProperties().get("district_house_number"));
        building.setSource((String) feature.getProperties().get("source"));
        building.setNote((String) feature.getProperties().get("note"));
        if (feature.getGeometry() != null) {
            if (feature.getGeometry() instanceof PolygonGeometry geometry) {
                List<List<List<Double>>> geometry_coords = geometry.getCoordinates();
                if (geometry_coords.isEmpty()) {
                    throw new IllegalArgumentException("Invalid coordinates");
                }
                building.setCoordinates(createPolygon(geometry_coords));
            } else {
                throw new IllegalArgumentException("Unsupported geometry type");
            }
        }
        return building;
    }
}
