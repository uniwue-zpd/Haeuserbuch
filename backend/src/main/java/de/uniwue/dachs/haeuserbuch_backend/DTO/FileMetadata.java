package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.enums.DocumentType;

public record FileMetadata(
    String documentCategory,
    DocumentType documentType,
    String dateCaptured,
    String dateCapturedPrecision,
    String dateFrom,
    String dateTo,
    String source,
    String collection,
    String signature,
    String creator,
    String rightsHolder,
    String license,
    Boolean downloadAllowed,
    String description,
    String caption,
    String sourceUrl
    ) {}
