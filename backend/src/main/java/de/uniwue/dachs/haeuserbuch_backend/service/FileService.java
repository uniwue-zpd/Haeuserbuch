package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.FileDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.FileMetadata;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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

    @Value("${haeuserbuch.files.upload-dir}")
    private String uploadDirValue;

    private final FileRepository fileRepository;
    private final BuildingRepository buildingRepository;

    public FileService(FileRepository fileRepository, FileMapper fileMapper, BuildingRepository buildingRepository) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
        this.buildingRepository = buildingRepository;
    }

    private final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "application/pdf"
    );

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
     * Uploads a single file, stores it on disk, and persists its metadata.
     * Entry point for uploads that carry additional per-file metadata.
     * @param file multipart file to upload
     * @param metadata optional descriptive metadata, may be {@code null}
     * @return created {@link FileDTO} object
     * @throws IllegalArgumentException if no file is provided or its type is not allowed
     * @throws IOException if the file cannot be stored
     */
    @Transactional
    public FileDTO uploadFile(MultipartFile file, FileMetadata metadata) throws IllegalArgumentException, IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file provided");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }

        File storedFile = storeFile(file, prepareUploadDir(), metadata);
        return fileMapper.toDTO(fileRepository.save(storedFile));
    }

    /**
     * Uploads multiple image files, stores them on disk, and persists their metadata.
     * @param files array of multipart files to upload
     * @param metadata optional per-file metadata, matched to {@code files} by index; may be {@code null}
     * @return list of created {@link FileDTO} objects
     * @throws IllegalArgumentException if no files are provided or the metadata count does not match the file count
     * @throws IOException if files cannot be stored
     */
    @Transactional
    public List<FileDTO> uploadFiles(MultipartFile[] files, List<FileMetadata> metadata) throws IllegalArgumentException, IOException {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No files provided");
        }
        if (metadata != null && metadata.size() != files.length) {
            throw new IllegalArgumentException(
                    "Metadata count (" + metadata.size() + ") does not match file count (" + files.length + ")");
        }

        List<File> uploadedFiles = new ArrayList<>();
        Path uploadDir = prepareUploadDir();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            if (file == null || file.isEmpty()) continue;

            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) continue;

            uploadedFiles.add(storeFile(file, uploadDir, metadata != null ? metadata.get(i) : null));
        }
        fileRepository.saveAll(uploadedFiles);
        return fileMapper.toDTOs(uploadedFiles);
    }

    private Path prepareUploadDir() throws IOException {
        Path uploadDir = Paths.get(uploadDirValue);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException ex) {
            throw new IOException("Could not create target directory " + uploadDir, ex);
        }
        return uploadDir;
    }

    private File storeFile(MultipartFile file, Path uploadDir, FileMetadata metadata) throws IOException {
        File storedFile = new File();
        writeContent(storedFile, file, uploadDir);
        if (metadata != null) applyMetadata(storedFile, metadata);
        return storedFile;
    }

    private void writeContent(File target, MultipartFile file, Path uploadDir) throws IOException {
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

        target.setOriginalName(originalFileName);
        target.setName(filename);
        target.setPath(targetPath.toString());
        target.setType(file.getContentType());
        target.setSize(file.getSize());
    }

    private void applyMetadata(File target, FileMetadata metadata) {
        target.setDocumentCategory(metadata.documentCategory());
        target.setDocumentType(metadata.documentType());
        target.setDateCaptured(metadata.dateCaptured());
        target.setDateCapturedPrecision(metadata.dateCapturedPrecision());
        target.setDateFrom(metadata.dateFrom());
        target.setDateTo(metadata.dateTo());
        target.setSource(metadata.source());
        target.setCollection(metadata.collection());
        target.setSignature(metadata.signature());
        target.setCreator(metadata.creator());
        target.setRightsHolder(metadata.rightsHolder());
        target.setLicense(metadata.license());
        target.setDownloadAllowed(metadata.downloadAllowed());
        target.setDescription(metadata.description());
        target.setCaption(metadata.caption());
        target.setSourceUrl(metadata.sourceUrl());
    }

    /**
     * Updates the metadata of a stored file and optionally replaces its content.
     * The file keeps its ID, so building attachments stay linked. When the content is replaced,
     * the old file is deleted from disk only after the transaction has committed.
     * @param id file ID
     * @param file optional new content, may be {@code null} or empty to keep the current content
     * @param metadata new metadata (full replacement), may be {@code null} to keep the current metadata
     * @return updated {@link FileDTO} object
     * @throws EntityNotFoundException if the file does not exist
     * @throws IllegalArgumentException if the new file type is not allowed
     * @throws IOException if the new file cannot be stored
     */
    @Transactional
    @CacheEvict(value = "buildings", allEntries = true)
    public FileDTO updateFile(Long id, MultipartFile file, FileMetadata metadata) throws IOException {
        File entity = getFileEntityById(id);
        String oldPath = null;
        if (file != null && !file.isEmpty()) {
            String contentType = file.getContentType();
            if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
                throw new IllegalArgumentException("File type not allowed: " + contentType);
            }
            oldPath = entity.getPath();
            writeContent(entity, file, prepareUploadDir());
        }
        if (metadata != null) applyMetadata(entity, metadata);
        File saved = fileRepository.save(entity);
        if (oldPath != null) deleteAfterCompletion(oldPath, entity.getPath());
        return fileMapper.toDTO(saved);
    }

    // Removes the old file once the DB change is committed; on rollback the new file is removed instead,
    // so the DB never points at a missing file.
    private void deleteAfterCompletion(String oldPath, String newPath) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                String obsolete = status == STATUS_COMMITTED ? oldPath : newPath;
                try {
                    Files.deleteIfExists(Paths.get(obsolete));
                } catch (IOException ignored) {
                    // A leftover file on disk is harmless, the DB stays consistent
                }
            }
        });
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

    /**
     * Searches for files by their names
     * @param query Query to be used
     * @return A {@link List} of {@link FileDTO} objects matching the query
     */
    public List<FileDTO> searchFiles(String query) {
        return fileRepository.searchFiles(query).stream()
                .map(fileMapper::toDTO)
                .toList();
    }
}
