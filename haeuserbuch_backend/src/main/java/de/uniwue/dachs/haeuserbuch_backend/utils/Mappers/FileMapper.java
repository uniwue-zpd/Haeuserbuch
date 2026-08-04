package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.FileDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.File;
import de.uniwue.dachs.haeuserbuch_backend.repository.FileRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class FileMapper {
    private final FileRepository fileRepository;

    public FileMapper(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File toFile(FileDTO fileDTO) {
        if (fileDTO == null || fileDTO.getId() == null) return null;
        return fileRepository.findById(fileDTO.getId()).orElse(null);
    }

    public List<File> toFiles(List<FileDTO> fileDTOS) {
        return fileDTOS.stream()
                .map(this::toFile)
                .filter(Objects::nonNull)
                .toList();
    }

    public FileDTO toDTO(File file) {
        if (file == null) return null;
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(file.getId());
        fileDTO.setOriginalName(file.getOriginalName());
        fileDTO.setName(file.getName());
        fileDTO.setType(file.getType());
        fileDTO.setSize(file.getSize());
        fileDTO.setCreatedDate(file.getCreatedDate());
        fileDTO.setCreatedBy(file.getCreatedBy());
        fileDTO.setLastModifiedDate(file.getLastModifiedDate());
        fileDTO.setLastModifiedBy(file.getLastModifiedBy());
        return fileDTO;
    }

    public List<FileDTO> toDTOs(List<File> files) {
        return files.stream()
                .map(this::toDTO)
                .filter(Objects::nonNull)
                .toList();
    }
}
