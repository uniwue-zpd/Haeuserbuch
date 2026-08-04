package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.FileDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.Building;
import de.uniwue.dachs.haeuserbuch_backend.model.File;
import de.uniwue.dachs.haeuserbuch_backend.repository.BuildingRepository;
import de.uniwue.dachs.haeuserbuch_backend.repository.FileRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.FileMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class FileService {
    private final FileMapper fileMapper;

    @Value("$config.files.upload-dir")
    private String uploadDirValue;

    private final FileRepository fileRepository;
    private final BuildingRepository buildingRepository;

    public FileService(FileRepository fileRepository, FileMapper fileMapper, BuildingRepository buildingRepository) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
        this.buildingRepository = buildingRepository;
    }

    /**
     * Returns metadata for all stored files.
     * @return list of stored files as {@link FileDTO} objects
     */
    public List<FileDTO> getAllFiles() {
        return fileMapper.toDTOs(fileRepository.findAll());
    }

    /**
     * Returns a paginated list of stored files.
     * @param pageable pagination and sorting information
     * @return page of {@link FileDTO} objects
     */
    public Page<FileDTO> getFiles(Pageable pageable) {
        return fileRepository.findAll(pageable)
                .map(fileMapper::toDTO);
    }

    /**
     * Returns a stored file by its ID.
     * @param id file ID
     * @return optional containing {@link FileDTO} if found
     */
    public Optional<FileDTO> getFileById(Long id) {
        return fileRepository.findById(id)
                .map(fileMapper::toDTO);
    }

    /**
     * Returns the internal {@link File} entity by ID.
     * Used internally when filesystem information is required.
     * @param id file ID
     * @return stored {@link File} entity
     */
    public File getFileEntityById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File not found with id: " + id));
    }

    /**
     * Uploads multiple image files, stores them on disk, and persists their metadata.
     * @param files array of multipart files to upload
     * @return list of created {@link FileDTO} objects
     * @throws IllegalArgumentException if no files are provided
     * @throws IOException if files cannot be stored
     */
    @Transactional
    public List<FileDTO> uploadFiles(MultipartFile[] files) throws IllegalArgumentException, IOException {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No files provided");
        }

        List<File> uploadedFiles = new ArrayList<>();
        Path uploadDir = Paths.get(uploadDirValue);

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException ex) {
            throw new IOException("Could not create target directory " + uploadDir, ex);
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) continue;

            String originalFileName = file.getOriginalFilename();
            String filename = UUID.randomUUID() + "-" + originalFileName;
            Path targetPath = uploadDir.resolve(filename);

            try (InputStream in = file.getInputStream()) {

                Files.copy(
                        in,
                        targetPath,
                        StandardCopyOption.REPLACE_EXISTING
                );

            } catch (IOException ex) {
                throw new IOException("Could not store file " + originalFileName, ex);
            }
            File savedFile = new File();
            savedFile.setOriginalName(originalFileName);
            savedFile.setName(filename);
            savedFile.setPath(targetPath.toString());
            savedFile.setType(contentType);
            savedFile.setSize(file.getSize());
            uploadedFiles.add(savedFile);
        }
        fileRepository.saveAll(uploadedFiles);
        return fileMapper.toDTOs(uploadedFiles);
    }

    /**
     * Deletes a file from filesystem and database.
     *
     * @param id file ID
     * @throws EntityNotFoundException if file does not exist
     * @throws IOException if deletion fails
     */
    @Transactional
    @CacheEvict(value = "buildings", allEntries = true)
    public void deleteFileById(Long id) throws EntityNotFoundException, IOException {
        File file = getFileEntityById(id);
        detachFileFromBuildings(id);
        Path filePath = Paths.get(file.getPath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new IOException("Could not delete file " + filePath, ex);
        }
        fileRepository.delete(file);
    }

    /**
     * Deletes multiple files from filesystem and database.
     * @param ids IDs of files to delete
     * @return deletion result grouped by status
     */
    @Transactional
    @CacheEvict(value = "buildings", allEntries = true)
    public Map<String, List<Long>> deleteFiles(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("No files to delete");
        }

        List<Long> deletedFiles = new ArrayList<>();
        List<Long> failedFiles = new ArrayList<>();
        List<Long> notFoundFiles = new ArrayList<>();

        for (Long id : ids) {
            Optional<File> optionalFile = fileRepository.findById(id);
            if (optionalFile.isPresent()) {
                File file = optionalFile.get();
                try {
                    detachFileFromBuildings(id);
                    Files.deleteIfExists(Paths.get(file.getPath()));
                    fileRepository.delete(file);
                    deletedFiles.add(id);
                } catch (IOException e) {
                    failedFiles.add(id);
                }
            } else {
                notFoundFiles.add(id);
            }
        }
        return Map.of(
                "success", deletedFiles,
                "fail", failedFiles,
                "notFound", notFoundFiles
        );
    }

    private void detachFileFromBuildings(Long fileId) {
        List<Building> buildings = buildingRepository.findByFilesId(fileId);
        if (buildings.isEmpty()) return;

        for (Building building : buildings) {
            building.getFiles().removeIf(file -> Objects.equals(file.getId(), fileId));
        }
        buildingRepository.saveAll(buildings);
    }

    /**
     * Loads file content as a {@link Resource}.
     * @param id file ID
     * @return file resource
     */
    public Resource getFileContent(Long id) {
        File file = getFileEntityById(id);
        try {
            Path filePath = Paths.get(file.getPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException(
                        "File with id " + id + " exists but is not readable"
                );
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid file path: " + file.getPath(), e);
        }
    }
}
