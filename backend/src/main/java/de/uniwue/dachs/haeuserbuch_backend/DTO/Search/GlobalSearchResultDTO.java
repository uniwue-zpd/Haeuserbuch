package de.uniwue.dachs.haeuserbuch_backend.DTO.Search;

import java.util.Set;

public record GlobalSearchResultDTO(
        SearchEntityType entityType,
        Long entityId,
        String title,
        String subtitle,
        Set<SearchField> matchedFields,
        String excerpt
) {}
