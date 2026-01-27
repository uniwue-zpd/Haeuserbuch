package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Data Transfer Object representing a citizenship record associated with a person
 */
@Getter
@Setter
public class CitizenshipDTO {
    private Long id;
    private String signature;
    private Long refNumber;
    private PersonPreviewDTO person;
    private SourceDTO primarySource;
    private SourceDTO secondarySource;
    private String date;
    private String entryText;
    private String addendum;
    private String internalNotes;
    private String generalNotes;
    private Date createdDate;
    private String createdBy;
    private Date lastModifiedDate;
    private String lastModifiedBy;
}
