package de.uniwue.dachs.haeuserbuch_backend.DTO.Register;

import java.util.List;

/**
 * A place of the place register with its related entries (persons or citizenships)
 */
public record PlaceRegisterDTO<T>(
        Long id,
        String realName,
        List<T> entries
) {}
