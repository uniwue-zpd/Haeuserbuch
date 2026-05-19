package de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO;

import lombok.Getter;
import lombok.Setter;
import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;

/**
 * DTO representing a {@link Citizenship} with only the most important attributes for preview purposes.
 */
@Getter
@Setter
public class CitizenshipPreviewDTO {
    private Long id;
    private String signature;
    private String refNumber;
}
