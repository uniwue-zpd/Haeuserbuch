package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingNameDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.BuildingNameSourceDTO;
import de.uniwue.dachs.haeuserbuch_backend.embeddable.BuildingNameSource;
import de.uniwue.dachs.haeuserbuch_backend.model.BuildingName;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BuildingNameMapper {
    private final SourceMapper sourceMapper;

    public BuildingNameMapper(SourceMapper sourceMapper) {
        this.sourceMapper = sourceMapper;
    }

    /**
     * Writes the DTOs into the name list of a building (used for create and update).
     * A name with a known id is updated in place, any other name is created,
     * and names missing from the DTOs are removed (orphanRemoval).
     */
    public void applyTo(List<BuildingName> names, List<BuildingNameDTO> dtos) {
        Map<Long, BuildingName> byId = new HashMap<>();
        names.forEach(name -> byId.put(name.getId(), name));

        List<BuildingName> result = new ArrayList<>();
        for (BuildingNameDTO dto : dtos == null ? List.<BuildingNameDTO>of() : dtos) {
            BuildingName name = dto.getId() != null ? byId.remove(dto.getId()) : null;
            if (name == null) name = new BuildingName();
            name.setName(clean(dto.getName()));
            name.setFromDate(clean(dto.getFromDate()));
            name.setToDate(clean(dto.getToDate()));
            name.getSources().clear();
            for (BuildingNameSourceDTO entry : dto.getSources() == null ? List.<BuildingNameSourceDTO>of() : dto.getSources()) {
                Source source = sourceMapper.SourceDTOToSource(entry.getSource());
                String excerpt = clean(entry.getExcerpt());
                if (source != null || excerpt != null) {
                    name.getSources().add(new BuildingNameSource(source, excerpt));
                }
            }
            result.add(name);
        }
        names.clear();
        names.addAll(result);
    }

    public List<BuildingNameDTO> BuildingNamesToDTOs(List<BuildingName> names) {
        return names.stream().map(name -> {
            BuildingNameDTO dto = new BuildingNameDTO();
            dto.setId(name.getId());
            dto.setName(name.getName());
            dto.setFromDate(name.getFromDate());
            dto.setToDate(name.getToDate());
            dto.setSources(name.getSources().stream().map(entry -> {
                BuildingNameSourceDTO sourceDto = new BuildingNameSourceDTO();
                sourceDto.setSource(sourceMapper.SourceToDTO(entry.getSource()));
                sourceDto.setExcerpt(entry.getExcerpt());
                return sourceDto;
            }).toList());
            return dto;
        }).toList();
    }

    /** Empty or whitespace-only form input → null, otherwise trimmed. */
    private static String clean(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
