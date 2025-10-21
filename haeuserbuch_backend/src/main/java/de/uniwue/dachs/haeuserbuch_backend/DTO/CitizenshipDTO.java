package de.uniwue.dachs.haeuserbuch_backend.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CitizenshipDTO {
    private Long id;
    private String signature;
    private Set<PersonDTO> persons = new HashSet<>();
    private SourceDTO source;
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
