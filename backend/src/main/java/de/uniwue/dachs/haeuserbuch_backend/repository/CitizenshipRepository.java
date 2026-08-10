package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.DTO.FullTextSearch.CitizenshipFullTextSearchResult;
import de.uniwue.dachs.haeuserbuch_backend.model.Citizenship;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitizenshipRepository extends JpaRepository<Citizenship, Long>, JpaSpecificationExecutor<Citizenship> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {
            "person",
            "primarySource",
            "secondarySource"
    })
    List<Citizenship> findAll();

    /**
     * Full-text search for citizenship register entries based on the query string.
     * @param query The search query string.
     * @return A {@link Page} object of {@link CitizenshipFullTextSearchResult} objects containing metadata.
     * **/
    @Query(value = """
        SELECT
            cz.id,
            cz.signature,
            cz.ref_number,
            ts_headline('german', cz.entry_text, websearch_to_tsquery('german', :query), 'HighlightAll=true') AS query_result
        FROM citizenship cz
        WHERE cz.full_text_vector @@ websearch_to_tsquery('german', :query)""",
        countQuery = """
        SELECT COUNT(*)
        FROM citizenship cz
        WHERE cz.full_text_vector @@ websearch_to_tsquery('german', :query)
        """, nativeQuery = true)
    Page<CitizenshipFullTextSearchResult> searchFullText(@Param("query") String query, Pageable pageable);

    /**
     * Performs an exact full-text search on citizenship entries using a regular expression.
     * <p>The search matches only complete words using PostgreSQL word boundaries
     * ({@code \m} and {@code \M}). Matching terms are highlighted in the returned
     * entry text by wrapping them with {@code <b>} tags.</p>
     * @param query the exact search term to find in the citizenship entry text
     * @param pageable pagination information including page number, page size, and sorting
     * @return a {@link Page} of {@link CitizenshipFullTextSearchResult} objects containing
     * the matching citizenship entries and highlighted text fragments
     */
    @Query(value = """
    SELECT
        cz.id,
        cz.signature,
        cz.ref_number,
        regexp_replace(
            cz.entry_text,
            '\\m' || :query || '\\M',
            '<b>' || :query || '</b>',
            'gi'
        ) AS query_result
    FROM citizenship cz
    WHERE cz.entry_text ~* ('\\m' || :query || '\\M')
    """,
    countQuery = """
    SELECT COUNT(*)
    FROM citizenship cz
    WHERE cz.entry_text ~* ('\\m' || :query || '\\M')
    """, nativeQuery = true)
    Page<CitizenshipFullTextSearchResult> searchFullTextExact(@Param("query") String query, Pageable pageable);
}
