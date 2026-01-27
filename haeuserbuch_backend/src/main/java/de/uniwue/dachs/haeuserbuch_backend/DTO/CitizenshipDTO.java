package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CitizenshipDTO {
    private Long id;
    private String signature;
    private PersonPreviewDTO person;
    private SourceDTO primarySource;
    private SourceDTO secondarySource;
    private Long number;
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
