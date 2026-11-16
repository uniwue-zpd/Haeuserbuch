package de.uniwue.dachs.haeuserbuch_backend.specification;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Bundles all optional search and filter criteria for citizenships.
 * Each non-null (and, for strings, non-blank) value is applied as an AND-condition.
 */
public record CitizenshipSearchCriteria(
        String refNumber,
        String signature,
        String naturalizedPerson,
        Long naturalizedPersonId,
        String naturalizedPersonJob,
        String naturalizedPersonOrigin,
        String guarantor,
        Long guarantorId,
        String guarantorJob,
        String guarantorOrigin,
        Integer yearNaturalization,
        Integer yearFrom,
        Integer yearTo,
        String primarySource,
        String secondarySource
) {
    /**
     * @return {@code true} if no criterion is set
     */
    public boolean isEmpty() {
        return Stream.<Object>of(
                refNumber, signature,
                naturalizedPerson, naturalizedPersonId, naturalizedPersonJob, naturalizedPersonOrigin,
                guarantor, guarantorId, guarantorJob, guarantorOrigin,
                yearNaturalization, yearFrom, yearTo, primarySource, secondarySource
        ).filter(Objects::nonNull).allMatch(v -> v instanceof String s && s.isBlank());
    }
}
