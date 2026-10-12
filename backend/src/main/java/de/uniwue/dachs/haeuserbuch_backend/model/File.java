package de.uniwue.dachs.haeuserbuch_backend.model;

import de.uniwue.dachs.haeuserbuch_backend.enums.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class File extends BaseEntity {
    private String originalName;
    private String name;
    private String path;
    private String type;
    private Long size;

    // Classification
    private String documentCategory;
    @Enumerated(EnumType.STRING)
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
    @Column(columnDefinition = "TEXT")
    private String description;
    private String caption;
    private String sourceUrl;
}
