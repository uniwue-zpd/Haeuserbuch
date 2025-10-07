package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.DTO.GeoJsonDTO.Feature;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CitizenshipDTO {
    private Long id;
    private Set<PersonDTO> persons = new HashSet<>();
    private SourceDTO source;
    private Feature place;
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
