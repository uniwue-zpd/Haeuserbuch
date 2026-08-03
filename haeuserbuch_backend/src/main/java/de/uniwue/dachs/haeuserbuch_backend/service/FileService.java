package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.model.File;
import de.uniwue.dachs.haeuserbuch_backend.repository.FileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {
    @Value("$config.files.upload-dir")
    private String uploadDir;

    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Resource getFileContent(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File with id " + id + " not found"));
        try {
            Path filePath = Paths.get(file.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File with id " + id + " exists but is not readable");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid file path: " + file.getFilePath(), e);
        }
    }
}
