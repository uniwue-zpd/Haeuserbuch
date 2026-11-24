package de.uniwue.dachs.haeuserbuch_backend.DTO.Register;

import java.util.List;

/**
 * A group of a register (e.g. a place or an occupation) with its related entries (persons or citizenships)
 */
public record RegisterDTO<T>(
        Long id,
        String name,
        List<T> entries
) {}
