package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingNameDTO;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.BuildingName;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BuildingNameMapper {
    public BuildingName buildingNameDTOToBuildingName(BuildingNameDTO buildingNameDTO) {
        if (buildingNameDTO == null) {
            return null;
        }
        BuildingName buildingName = new BuildingName();
        buildingName.setName(buildingNameDTO.getName());
        buildingName.setSource(buildingNameDTO.getSource());
        return buildingName;
    }

    public Set<BuildingName> buildingNameDTOsToBuildingNames(Set<BuildingNameDTO> buildingNameDTOs) {
        if (buildingNameDTOs == null || buildingNameDTOs.isEmpty()) {
            return new HashSet<>();
        }
        return buildingNameDTOs.stream().map(this::buildingNameDTOToBuildingName).collect(Collectors.toSet());
    }

    public BuildingNameDTO buildingNameToDTO(BuildingName buildingName) {
        if (buildingName == null) {
            return null;
        }
        BuildingNameDTO dto = new BuildingNameDTO();
        dto.setName(buildingName.getName());
        dto.setSource(buildingName.getSource());
        return dto;
    }

    public Set<BuildingNameDTO> buildingNamesToBuildingNameDTOs(Set<BuildingName> buildingNames) {
        if (buildingNames == null || buildingNames.isEmpty()) {
            return new HashSet<>();
        }
        return buildingNames.stream().map(this::buildingNameToDTO).collect(Collectors.toSet());
    }
}
