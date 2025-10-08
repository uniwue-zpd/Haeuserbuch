package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.SourceDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Source;
import de.uniwue.dachs.haeuserbuch_backend.repository.SourceRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SourceMapper {
    private final SourceRepository sourceRepository;

    public SourceMapper(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    // Convert a SourceDTO to a Source object
    public Source SourceDTOToSource(SourceDTO sourceDTO) {
        if (sourceDTO == null || sourceDTO.getId() == null) {
            return null;
        }
        return sourceRepository.findById(sourceDTO.getId()).orElse(null);
    }

    public Set<Source> SourceDTOsToSources(Set<SourceDTO> sourceDTOs) {
        return sourceDTOs.stream().map(this::SourceDTOToSource).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    // Convert a Source object to a SourceDTO
    public SourceDTO SourceToDTO(Source source) {
        SourceDTO sourceDTO = new SourceDTO();
        sourceDTO.setId(source.getId());
        sourceDTO.setTitle(source.getTitle());
        return sourceDTO;
    }

    // Convert a set of SourceDTOs to a set of Source objects
    public Set<SourceDTO> SourcesToDTOs(Set<Source> sources) {
        return sources.stream().map(this::SourceToDTO).collect(Collectors.toSet());
    }
}
