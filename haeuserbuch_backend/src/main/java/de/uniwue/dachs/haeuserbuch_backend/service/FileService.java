package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.FileDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.File;
import de.uniwue.dachs.haeuserbuch_backend.repository.FileRepository;
import de.uniwue.dachs.haeuserbuch_backend.utils.Mappers.FileMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
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

    public FileService(FileRepository fileRepository, FileMapper fileMapper) {
        this.fileRepository = fileRepository;
        this.fileMapper = fileMapper;
    }

    /**
     * Returns metadata for all stored files.
     * @return list of all stored files as {@link FileDTO} objects
     */
    public List<FileDTO> getAllFiles() {
        return fileMapper.toDTOs(fileRepository.findAll());
    }

    /**
     * Returns a paginated list of stored files.
     * @param pageable pagination and sorting information
     * @return page containing {@link FileDTO} objects
     */
    public Page<FileDTO> getFiles(Pageable pageable) {
        Page<File> filePage = fileRepository.findAll(pageable);
        return filePage.map(fileMapper::toDTO);
    }

    /**
     * Returns the metadata of a stored file by its ID.
     * @param id ID of the file
     * @return an {@link Optional} containing the corresponding {@link FileDTO},
     * or an empty {@link Optional} if no file exists with the given ID
     */
    public Optional<FileDTO> getFileById(Long id) {
        return fileRepository.findById(id).map(fileMapper::toDTO);
    }

    /**
     * Uploads multiple image files, stores them on disk, and persists their metadata.
     * Only non-empty files with a content type starting with {@code image/} are processed.
     * Each stored file gets a UUID-prefixed filename to avoid name collisions. For every
     * successfully stored file, a {@link File} entity is created and saved in the database.
     * @param files array of multipart files to upload
     * @return list of created {@link FileDTO} objects for all successfully processed files
     * @throws IllegalArgumentException if {@code files} is {@code null} or empty
     * @throws IOException if the upload directory cannot be created or a file cannot be written
     */
    @Transactional
    public List<FileDTO> uploadFiles(MultipartFile[] files) throws IllegalArgumentException, IOException {
        if (files == null || files.length == 0) throw new IllegalArgumentException("No files provided");

        List<File> uploadedFiles = new ArrayList<>();
        Path uploadDir = Paths.get(uploadDirValue);

        try {
            Files.createDirectories(uploadDir);
        } catch (IOException ex) {
            throw new IOException("Could not create target directory" + uploadDir, ex);
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) continue;

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) continue;

            String originalFileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID() + "-" + originalFileName;
            Path targetPath = uploadDir.resolve(uuid);

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new IOException("Could not store file " + originalFileName + ". Please try again!", ex);
            }

            File savedFile = new File();
            savedFile.setOriginalName(originalFileName);
            savedFile.setName(uuid);
            savedFile.setPath(targetPath.toString());
            savedFile.setType(contentType);
            savedFile.setSize(file.getSize());
            uploadedFiles.add(savedFile);
        }
        fileRepository.saveAll(uploadedFiles);
        return fileMapper.toDTOs(uploadedFiles);
    }

    /**
     * Deletes the stored file and its corresponding {@link File} entity.
     * The physical file is removed from the file system before the database
     * record is deleted.
     * @param id ID of the file to delete
     * @throws EntityNotFoundException if no {@link File} exists with the given ID
     * @throws IOException if the file cannot be deleted from the file system
     */
    @Transactional
    public void deleteFileById(Long id) throws EntityNotFoundException, IOException {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File not found with id: " + id));

        Path filePath = Paths.get(file.getPath());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new IOException("Could not delete file " + filePath, ex);
        }
        fileRepository.deleteById(id);
    }

    /**
     * Deletes multiple stored files and their corresponding {@link File} entities.
     * Each file is removed from both the file system and the database. The result
     * contains separate lists of successfully deleted files, files that could not
     * be deleted, and IDs that do not exist.
     * @param ids IDs of the files to delete
     * @return a map containing the keys {@code success}, {@code fail}, and
     * {@code notFound}, each mapping to the corresponding list of file IDs
     * @throws IllegalArgumentException if the provided ID set is {@code null} or empty
     */
    @Transactional
    public Map<String, List<Long>> deleteFiles(Set<Long> ids) throws IllegalArgumentException {
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

    /**
     * Loads the file content as a {@link Resource} for the given file ID.
     * The method first resolves the file metadata from the database, then
     * maps the stored file path to a {@link UrlResource}. It validates that
     * the underlying file exists and is readable before returning the resource.
     * @param id ID of the file to load
     * @return readable file content as a {@link Resource}
     * @throws EntityNotFoundException if no file metadata exists for the given ID
     * @throws RuntimeException if the file path is invalid, missing, or not readable
     */
    public Resource getFileContent(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File with id " + id + " not found"));
        try {
            Path filePath = Paths.get(file.getPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File with id " + id + " exists but is not readable");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid file path: " + file.getPath(), e);
        }
    }
}
