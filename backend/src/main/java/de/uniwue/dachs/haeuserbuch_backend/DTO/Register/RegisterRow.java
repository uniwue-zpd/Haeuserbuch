package de.uniwue.dachs.haeuserbuch_backend.DTO.Register;

/**
 * Flat query row of a group (e.g. a place or an occupation) listed in a register
 */
public record RegisterRow(
        Long id,
        String name
) {}
