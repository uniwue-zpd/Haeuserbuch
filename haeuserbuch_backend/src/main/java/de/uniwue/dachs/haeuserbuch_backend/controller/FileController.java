package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.FileDTO;
import de.uniwue.dachs.haeuserbuch_backend.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }


    /**
     * Returns all stored files.
     * @return list of all files
     */
    @GetMapping("/all")
    public ResponseEntity<List<FileDTO>> getAllFiles() {
        return ResponseEntity.ok(fileService.getAllFiles());
    }


    /**
     * Returns a paginated list of stored files.
     * @param pageable pagination parameters
     * @return paginated file list
     */
    @GetMapping
    public ResponseEntity<Page<FileDTO>> getFiles(Pageable pageable) {
        return ResponseEntity.ok(fileService.getFiles(pageable));
    }


    /**
     * Returns file metadata by ID.
     * @param id file ID
     * @return file metadata or 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<FileDTO> getFileById(@PathVariable Long id) {
        Optional<FileDTO> file = fileService.getFileById(id);
        return file
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * Uploads multiple image files.
     * @param files multipart files
     * @return created file metadata
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileDTO>> uploadFiles(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<FileDTO> uploadedFiles = fileService.uploadFiles(files);
        return ResponseEntity
                .status(201)
                .body(uploadedFiles);
    }


    /**
     * Deletes a file by ID.
     * @param id file ID
     * @return 204 after successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFileById(@PathVariable Long id) throws IOException {
        fileService.deleteFileById(id);
        return ResponseEntity.status(204).build();
    }


    /**
     * Deletes multiple files by IDs.
     *
     * @param ids file IDs
     * @return deletion result
     */
    @DeleteMapping
    public ResponseEntity<Map<String, List<Long>>> deleteFiles(@RequestBody Set<Long> ids) {
        return ResponseEntity.ok(fileService.deleteFiles(ids));
    }


    /**
     * Returns binary file content.
     * @param id file ID
     * @return file resource
     */
    @GetMapping("/{id}/content")
    public ResponseEntity<Resource> getFileContentById(@PathVariable Long id) {
        FileDTO file = fileService.getFileById(id).orElse(null);
        if (file == null) return ResponseEntity.notFound().build();
        Resource resource = fileService.getFileContent(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .body(resource);
    }

    /**
     * Returns a list of files matching the search query.
     * @param query Query to be used
     * @return A {@link ResponseEntity} containing a list of {@link FileDTO} objects matching the search query.
     * The response will have an HTTP status of 200 (OK) if the search is successful, or 404 (Not Found) if no files match the query.
     */
    @GetMapping("/search")
    public ResponseEntity<List<FileDTO>> searchFiles(@RequestParam("query") String query) {
        return ResponseEntity.ok(fileService.searchFiles(query));
    }
}
