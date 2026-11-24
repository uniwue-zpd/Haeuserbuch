package de.uniwue.dachs.haeuserbuch_backend.DTO.Register;

/**
 * Flat query row linking a place to one related entry (e.g. a person), used to build the place register
 */
public record PlaceEntryRow(
        Long placeId,
        Long entryId,
        String entryName
) {}
