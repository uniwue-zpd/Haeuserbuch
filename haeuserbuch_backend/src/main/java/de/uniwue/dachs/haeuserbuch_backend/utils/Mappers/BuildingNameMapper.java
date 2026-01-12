package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingNameDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.BuildingName;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingNameRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BuildingNameMapper {
    private final BuildingNameRepository buildingNameRepository;
    private final SourceMapper sourceMapper;

    public BuildingNameMapper(BuildingNameRepository buildingNameRepository, SourceMapper sourceMapper) {
        this.buildingNameRepository = buildingNameRepository;
        this.sourceMapper = sourceMapper;
    }

    public BuildingName BuildingNameDTOToBuildingName(BuildingNameDTO buildingNameDTO) {
        if (buildingNameDTO == null) return null;
        if (buildingNameDTO.getId() == null) {
            BuildingName buildingName = new BuildingName();
            buildingName.setName(buildingNameDTO.getName());
            buildingName.setSource(sourceMapper.SourceDTOToSource(buildingNameDTO.getSource()));
            buildingName.setFromDate(buildingNameDTO.getFromDate());
            buildingName.setToDate(buildingNameDTO.getToDate());
            return buildingName;
        } else {
            return buildingNameRepository.findById(buildingNameDTO.getId()).orElse(null);
        }
    }

    public Set<BuildingName> BuildingNameDTOsToBuildingNames(Set<BuildingNameDTO> buildingNameDTOs) {
        if (buildingNameDTOs == null) return null;
        return buildingNameDTOs.stream().map(this::BuildingNameDTOToBuildingName).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public BuildingNameDTO BuildingNameToDTO(BuildingName buildingName) {
        if (buildingName == null) return null;
        BuildingNameDTO buildingNameDTO = new BuildingNameDTO();
        buildingNameDTO.setId(buildingName.getId());
        buildingNameDTO.setName(buildingName.getName());
        buildingNameDTO.setSource(sourceMapper.SourceToDTO(buildingName.getSource()));
        buildingNameDTO.setFromDate(buildingName.getFromDate());
        buildingNameDTO.setToDate(buildingName.getToDate());
        return buildingNameDTO;
    }

    public Set<BuildingNameDTO> BuildingNamesToDTOs(Set<BuildingName> buildingNames) {
        if (buildingNames == null) return null;
        return buildingNames.stream().map(this::BuildingNameToDTO).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
