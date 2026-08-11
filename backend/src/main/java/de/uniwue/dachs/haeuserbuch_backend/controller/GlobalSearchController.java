package de.uniwue.dachs.haeuserbuch_backend.controller;

import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.GlobalSearchResponseDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchEntityType;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchField;
import de.uniwue.dachs.haeuserbuch_backend.service.GlobalSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/search")
public class GlobalSearchController {
    private final GlobalSearchService searchService;

    public GlobalSearchController(GlobalSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<GlobalSearchResponseDTO> search(
            @RequestParam String query,
            @RequestParam(required = false) String fields,
            @RequestParam(required = false) String types,
            @RequestParam(defaultValue = "false") boolean exactFullText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "true") boolean includeFacets
    ) {
        return ResponseEntity.ok(searchService.search(
                query,
                parseEnums(fields, SearchField.class, "fields"),
                parseEnums(types, SearchEntityType.class, "types"),
                exactFullText,
                page,
                size,
                includeFacets
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh() {
        searchService.requestRefresh();
        return ResponseEntity.accepted().build();
    }

    private <E extends Enum<E>> Set<E> parseEnums(String rawValue, Class<E> enumType, String parameterName) {
        if (rawValue == null || rawValue.isBlank()) return EnumSet.noneOf(enumType);
        Set<E> values = EnumSet.noneOf(enumType);
        try {
            for (String rawPart : rawValue.split(",")) {
                if (!rawPart.isBlank()) values.add(Enum.valueOf(enumType, rawPart.trim().toUpperCase(Locale.ROOT)));
            }
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(BAD_REQUEST, "Unsupported value in " + parameterName);
        }
        return values;
    }
}
