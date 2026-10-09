package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.enums.DocumentType;
import de.uniwue.dachs.haeuserbuch_backend.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDTO extends BaseEntity {
    private String originalName;
    private String name;
    private String type;
    private Long size;

    // Classification
    private String documentCategory;
    private DocumentType documentType;

    // Dates
    private String dateCaptured;
    private String dateCapturedPrecision;
    private String dateFrom;
    private String dateTo;

    // Provenance
    private String source;
    private String collection;
    private String signature;

    // Authorship
    private String creator;
    private String rightsHolder;
    private String license;
    private Boolean downloadAllowed;

    // Description
    private String description;
    private String caption;
    private String sourceUrl;
}
