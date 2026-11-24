package de.uniwue.dachs.haeuserbuch_backend.DTO.Register;

/**
 * Flat query row of a place listed in the place register
 */
public record PlaceRegisterRow(
        Long id,
        String realName
) {}
