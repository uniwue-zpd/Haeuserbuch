package de.uniwue.dachs.haeuserbuch_backend.service;

import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.GlobalSearchResponseDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.GlobalSearchResultDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchEntityType;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchFacetsDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchField;
import de.uniwue.dachs.haeuserbuch_backend.repository.GlobalSearchRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.GlobalSearchRefreshCoordinator;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class GlobalSearchService {
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\s\"'-]");

    private final GlobalSearchRepository searchRepository;
    private final GlobalSearchRefreshCoordinator refreshCoordinator;

    public GlobalSearchService(
            GlobalSearchRepository searchRepository,
            GlobalSearchRefreshCoordinator refreshCoordinator
    ) {
        this.searchRepository = searchRepository;
        this.refreshCoordinator = refreshCoordinator;
    }

    public GlobalSearchResponseDTO search(
            String query,
            Set<SearchField> requestedFields,
            Set<SearchEntityType> requestedTypes,
            boolean exactFullText,
            int page,
            int size,
            boolean includeFacets
    ) {
        String normalizedQuery = query == null ? "" : query.trim();
        if (normalizedQuery.length() < 2 || normalizedQuery.length() > 200) {
            throw new ResponseStatusException(BAD_REQUEST, "Search query must contain between 2 and 200 characters");
        }
        if (page < 0) throw new ResponseStatusException(BAD_REQUEST, "Page must not be negative");
        if (size < 1 || size > 50) throw new ResponseStatusException(BAD_REQUEST, "Size must contain between 1 and 50 results");

        Set<SearchField> fields = requestedFields == null || requestedFields.isEmpty()
                ? EnumSet.allOf(SearchField.class)
                : EnumSet.copyOf(requestedFields);
        Set<SearchEntityType> types = requestedTypes == null || requestedTypes.isEmpty()
                ? EnumSet.allOf(SearchEntityType.class)
                : EnumSet.copyOf(requestedTypes);
        String fullTextQuery = sanitizeFullTextQuery(normalizedQuery);

        List<GlobalSearchResultDTO> content = searchRepository.search(
                normalizedQuery,
                escapeLikePattern(normalizedQuery),
                fullTextQuery,
                fields,
                types,
                exactFullText,
                page,
                size
        );
        long totalElements = searchRepository.count(
                normalizedQuery,
                escapeLikePattern(normalizedQuery),
                fullTextQuery,
                fields,
                types,
                exactFullText
        );
        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        SearchFacetsDTO facets = includeFacets
                ? new SearchFacetsDTO(
                        searchRepository.countByEntityType(normalizedQuery, escapeLikePattern(normalizedQuery), fullTextQuery, fields, types, exactFullText),
                        searchRepository.countByField(normalizedQuery, escapeLikePattern(normalizedQuery), fullTextQuery, fields, types, exactFullText)
                )
                : null;

        return new GlobalSearchResponseDTO(content, page, size, totalElements, totalPages, facets);
    }

    public void requestRefresh() {
        refreshCoordinator.requestRefresh();
    }

    private String sanitizeFullTextQuery(String query) {
        return query
                .replaceAll(HTML_TAG_PATTERN.pattern(), "")
                .replaceAll(SPECIAL_CHAR_PATTERN.pattern(), "")
                .trim();
    }

    private String escapeLikePattern(String query) {
        return query.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
