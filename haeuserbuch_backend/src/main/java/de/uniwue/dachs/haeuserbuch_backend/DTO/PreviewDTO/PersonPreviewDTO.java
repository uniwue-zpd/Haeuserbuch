package de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object containing basic information about a person for preview purposes
 */
@Getter
@Setter
public class PersonPreviewDTO {
    private Long id;
    private String firstName;
    private String lastName;
}
