package de.uniwue.dachs.haeuserbuch_backend.DTO.Search;

import java.util.Map;

public record SearchFacetsDTO(
        Map<SearchEntityType, Long> entityTypes,
        Map<SearchField, Long> fields
) {}
