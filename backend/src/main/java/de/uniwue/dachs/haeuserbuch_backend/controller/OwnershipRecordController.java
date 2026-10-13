package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.OwnershipRecordDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.OwnershipRecord;
import de.uniwue.dachs.haeuserbuch_backend.service.OwnershipRecordService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ownership_records")
public class OwnershipRecordController {

    private final OwnershipRecordService ownershipRecordService;

    public OwnershipRecordController(OwnershipRecordService ownershipRecordService) {
        this.ownershipRecordService = ownershipRecordService;
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<?> create(@RequestBody String xml) {
        List<String> errors = ownershipRecordService.validate(xml);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("valid", false, "errors", errors));
        }
        OwnershipRecord record = ownershipRecordService.create(xml);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(record.getEntryXml());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<OwnershipRecordDTO> getById(@PathVariable Long id) {
        return ownershipRecordService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @GetMapping(value = "/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getXmlById(@PathVariable Long id) {
        return ownershipRecordService.getXmlById(id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }

    @PostMapping(path = "/validate", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateXml(@RequestBody String xml) {
        List<String> errors = ownershipRecordService.validate(xml);
        if (errors.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of("valid", true)
            );
        }
        return ResponseEntity.ok(
                Map.of("valid", false, "errors", errors)
        );
    }
}
