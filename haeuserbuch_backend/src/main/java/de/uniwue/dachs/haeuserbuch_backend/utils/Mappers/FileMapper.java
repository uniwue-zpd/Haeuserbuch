package de.uniwue.dachs.haeuserbuch_backend.utils.Mappers;

import de.uniwue.dachs.haeuserbuch_backend.DTO.FileDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.FilePreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.File;
import de.uniwue.dachs.haeuserbuch_backend.repository.FileRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public FilePreviewDTO toPreviewDTO(File file) {
        if (file == null) return null;
        FilePreviewDTO dto = new FilePreviewDTO();
        dto.setId(file.getId());
        dto.setOriginalName(file.getOriginalName());
        return dto;
    }

    public Set<FilePreviewDTO> toPreviewDTOs(Set<File> files) {
        if (files == null) return new HashSet<>();
        return files.stream()
                .map(this::toPreviewDTO)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());
    }

    public Set<File> toFiles(Set<FilePreviewDTO> filePreviews) {
        if (filePreviews == null || filePreviews.isEmpty()) {
            return new HashSet<>();
        }

        Set<Long> ids = filePreviews.stream()
                .map(FilePreviewDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (ids.size() != filePreviews.size()) {
            throw new IllegalArgumentException("Each file reference must contain an existing file ID");
        }

        List<File> files = fileRepository.findAllById(ids);
        if (files.size() != ids.size()) {
            throw new IllegalArgumentException("One or more referenced files do not exist");
        }

        return new HashSet<>(files);
    }
}
