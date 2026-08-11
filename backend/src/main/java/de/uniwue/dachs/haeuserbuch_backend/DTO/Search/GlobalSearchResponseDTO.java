package de.uniwue.dachs.haeuserbuch_backend.DTO.Search;

import java.util.List;

public record GlobalSearchResponseDTO(
        List<GlobalSearchResultDTO> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        SearchFacetsDTO facets
) {}
