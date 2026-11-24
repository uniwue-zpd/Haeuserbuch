package de.uniwue.dachs.haeuserbuch_backend.DTO.Register;

/**
 * Flat query row linking a person (citizen, guarantor or mentioned person) to a citizenship, used to build the place register
 */
public record CitizenshipPersonRow(
        Long personId,
        Long citizenshipId,
        String signature,
        String refNumber
) {}
