package de.uniwue.dachs.haeuserbuch_backend.DTO;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Data Transfer Object representing a citizenship record associated with a person
 */
@Getter
@Setter
public class CitizenshipDTO extends BaseEntity {
    private String signature;
    private String refNumber;
    private PersonPreviewDTO person;
    private Set<PersonPreviewDTO> mentionedPersons;
    private SourceDTO primarySource;
    private SourceDTO secondarySource;
    private String dateNaturalization;
    private String dateMisc;
    private String entryText;
    private String addendum;
}
