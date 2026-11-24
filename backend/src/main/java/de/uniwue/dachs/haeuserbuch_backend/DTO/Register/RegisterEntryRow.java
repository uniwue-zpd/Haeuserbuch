package de.uniwue.dachs.haeuserbuch_backend.DTO.Register;

/**
 * Flat query row linking a register group (e.g. a place or an occupation) to one related entry (e.g. a person)
 */
public record RegisterEntryRow(
        Long groupId,
        Long entryId,
        String entryName
) {}
