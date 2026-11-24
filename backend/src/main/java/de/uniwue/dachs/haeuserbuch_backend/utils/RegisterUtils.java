package de.uniwue.dachs.haeuserbuch_backend.utils;

import de.uniwue.dachs.haeuserbuch_backend.DTO.PreviewDTO.PersonPreviewDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Register.RegisterEntryRow;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helpers shared by all registers (places, occupations, ...)
 */
public final class RegisterUtils {
    private RegisterUtils() {}

    /** Search term for the register queries; an empty string matches every name */
    public static String normalizeName(String name) {
        return name == null ? "" : name.trim();
    }

    /** Register order is fixed in the query, so a client {@code sort} must not be appended */
    public static Pageable unsorted(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }

    /** groupId -> IDs of the persons of that group */
    public static Map<Long, Set<Long>> personIdsByGroup(Stream<RegisterEntryRow> rows) {
        return rows.collect(Collectors.groupingBy(
                RegisterEntryRow::groupId,
                Collectors.mapping(RegisterEntryRow::entryId, Collectors.toSet())
        ));
    }

    /** groupId -> persons of that group, deduplicated by ID and sorted by name */
    public static Map<Long, List<PersonPreviewDTO>> personsByGroup(
            Stream<RegisterEntryRow> rows,
            Function<RegisterEntryRow, PersonPreviewDTO> mapper
    ) {
        Map<Long, Map<Long, PersonPreviewDTO>> byGroup = new HashMap<>();
        rows.forEach(row -> byGroup
                .computeIfAbsent(row.groupId(), id -> new HashMap<>())
                .putIfAbsent(row.entryId(), mapper.apply(row)));

        Map<Long, List<PersonPreviewDTO>> result = new HashMap<>();
        byGroup.forEach((groupId, persons) -> result.put(groupId, persons.values().stream()
                .sorted(Comparator.comparing(
                        PersonPreviewDTO::getFullName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList()));
        return result;
    }
}
