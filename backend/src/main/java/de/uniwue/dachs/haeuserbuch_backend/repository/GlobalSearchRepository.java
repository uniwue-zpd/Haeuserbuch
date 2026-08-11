package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.GlobalSearchResultDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchEntityType;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchField;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class GlobalSearchRepository {
    private static final String CANDIDATES_CTE = """
            WITH candidates AS (
                SELECT d.*,
                       EXISTS (
                           SELECT 1 FROM UNNEST(d.title_values) title_value
                            WHERE LOWER(title_value) = LOWER(:query)
                       ) AS title_exact,
                       EXISTS (
                           SELECT 1 FROM UNNEST(d.title_values) title_value
                            WHERE LOWER(title_value) LIKE LOWER(:likeQuery) || '%' ESCAPE '\\'
                       ) AS title_prefix,
                       LOWER(d.title_text) LIKE '%' || LOWER(:likeQuery) || '%' ESCAPE '\\' AS title_match,
                       LOWER(d.metadata_text) LIKE '%' || LOWER(:likeQuery) || '%' ESCAPE '\\' AS metadata_match,
                       CASE
                           WHEN NOT :fullTextEnabled OR d.full_text_vector IS NULL OR :fullTextQuery = '' THEN FALSE
                           WHEN :exactFullText THEN REGEXP_REPLACE(COALESCE(d.full_text, ''), '<[^>]*>', ' ', 'g')
                                                    ~* ('\\m' || :fullTextQuery || '\\M')
                           ELSE d.full_text_vector @@ websearch_to_tsquery('german', :fullTextQuery)
                       END AS full_text_match,
                       CASE
                           WHEN NOT :fullTextEnabled OR d.full_text_vector IS NULL
                                OR :exactFullText OR :fullTextQuery = '' THEN 0.0
                           ELSE ts_rank_cd(d.full_text_vector, websearch_to_tsquery('german', :fullTextQuery))
                       END AS full_text_rank
                  FROM global_search_document d
            ), ranked AS (
                SELECT candidates.*,
                       CASE
                           WHEN :titleEnabled AND title_exact THEN 5
                           WHEN :titleEnabled AND title_prefix THEN 4
                           WHEN :titleEnabled AND title_match THEN 3
                           WHEN :metadataEnabled AND metadata_match THEN 2
                           WHEN :fullTextEnabled AND full_text_match THEN 1
                           ELSE 0
                       END AS rank_tier
                  FROM candidates
            )
            """;

    private static final String ENABLED_MATCH = """
            ((:titleEnabled AND title_match)
             OR (:metadataEnabled AND metadata_match)
             OR (:fullTextEnabled AND full_text_match))
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GlobalSearchRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void refreshSearchDocuments() {
        jdbcTemplate.getJdbcTemplate().execute("REFRESH MATERIALIZED VIEW CONCURRENTLY global_search_document");
    }

    public List<GlobalSearchResultDTO> search(
            String query,
            String escapedLikeQuery,
            String fullTextQuery,
            Set<SearchField> fields,
            Set<SearchEntityType> types,
            boolean exactFullText,
            int page,
            int size
    ) {
        String sql = CANDIDATES_CTE + """
                SELECT entity_type, entity_id, title, subtitle,
                       title_match, metadata_match, full_text_match,
                       CASE WHEN full_text_match THEN
                           ts_headline(
                               'german',
                               REPLACE(REPLACE(REPLACE(
                                   REGEXP_REPLACE(COALESCE(full_text, ''), '<[^>]*>', ' ', 'g'),
                                   '&', '&amp;'), '<', '&lt;'), '>', '&gt;'),
                               CASE WHEN :exactFullText
                                    THEN phraseto_tsquery('german', :fullTextQuery)
                                    ELSE websearch_to_tsquery('german', :fullTextQuery)
                               END,
                               'StartSel=<mark>, StopSel=</mark>, MaxFragments=2, MinWords=8, MaxWords=24, FragmentDelimiter= … '
                           )
                       END AS excerpt
                  FROM ranked
                 WHERE entity_type IN (:types)
                   AND """ + ENABLED_MATCH + """
                 ORDER BY rank_tier DESC, full_text_rank DESC, LOWER(title), entity_id, entity_type
                 LIMIT :limit OFFSET :offset
                """;

        MapSqlParameterSource parameters = parameters(query, escapedLikeQuery, fullTextQuery, fields, types, exactFullText)
                .addValue("limit", size)
                .addValue("offset", (long) page * size);

        return jdbcTemplate.query(sql, parameters, (resultSet, rowNumber) -> {
            Set<SearchField> matchedFields = new LinkedHashSet<>();
            if (fields.contains(SearchField.TITLE) && resultSet.getBoolean("title_match")) matchedFields.add(SearchField.TITLE);
            if (fields.contains(SearchField.METADATA) && resultSet.getBoolean("metadata_match")) matchedFields.add(SearchField.METADATA);
            if (fields.contains(SearchField.FULL_TEXT) && resultSet.getBoolean("full_text_match")) matchedFields.add(SearchField.FULL_TEXT);
            return new GlobalSearchResultDTO(
                    SearchEntityType.valueOf(resultSet.getString("entity_type")),
                    resultSet.getLong("entity_id"),
                    resultSet.getString("title"),
                    resultSet.getString("subtitle"),
                    matchedFields,
                    resultSet.getString("excerpt")
            );
        });
    }

    public long count(
            String query,
            String escapedLikeQuery,
            String fullTextQuery,
            Set<SearchField> fields,
            Set<SearchEntityType> types,
            boolean exactFullText
    ) {
        String sql = CANDIDATES_CTE + """
                SELECT COUNT(*)
                  FROM ranked
                 WHERE entity_type IN (:types)
                   AND """ + ENABLED_MATCH;
        Long count = jdbcTemplate.queryForObject(
                sql,
                parameters(query, escapedLikeQuery, fullTextQuery, fields, types, exactFullText),
                Long.class
        );
        return count == null ? 0 : count;
    }

    public Map<SearchEntityType, Long> countByEntityType(
            String query,
            String escapedLikeQuery,
            String fullTextQuery,
            Set<SearchField> fields,
            Set<SearchEntityType> types,
            boolean exactFullText
    ) {
        String sql = CANDIDATES_CTE + """
                SELECT entity_type, COUNT(*) AS result_count
                  FROM ranked
                 WHERE """ + ENABLED_MATCH + """
                 GROUP BY entity_type
                """;
        Map<SearchEntityType, Long> counts = zeroEntityCounts();
        jdbcTemplate.query(
                sql,
                parameters(query, escapedLikeQuery, fullTextQuery, fields, types, exactFullText),
                (RowCallbackHandler) resultSet -> counts.put(
                        SearchEntityType.valueOf(resultSet.getString("entity_type")),
                        resultSet.getLong("result_count")
                )
        );
        return counts;
    }

    public Map<SearchField, Long> countByField(
            String query,
            String escapedLikeQuery,
            String fullTextQuery,
            Set<SearchField> fields,
            Set<SearchEntityType> types,
            boolean exactFullText
    ) {
        String sql = CANDIDATES_CTE + """
                SELECT COUNT(*) FILTER (WHERE title_match) AS title_count,
                       COUNT(*) FILTER (WHERE metadata_match) AS metadata_count,
                       COUNT(*) FILTER (WHERE full_text_match) AS full_text_count
                  FROM ranked
                 WHERE entity_type IN (:types)
                """;
        return jdbcTemplate.queryForObject(
                sql,
                parameters(query, escapedLikeQuery, fullTextQuery, fields, types, exactFullText),
                (resultSet, rowNumber) -> {
                    Map<SearchField, Long> counts = new EnumMap<>(SearchField.class);
                    counts.put(SearchField.TITLE, resultSet.getLong("title_count"));
                    counts.put(SearchField.METADATA, resultSet.getLong("metadata_count"));
                    counts.put(SearchField.FULL_TEXT, resultSet.getLong("full_text_count"));
                    return counts;
                }
        );
    }

    private MapSqlParameterSource parameters(
            String query,
            String escapedLikeQuery,
            String fullTextQuery,
            Set<SearchField> fields,
            Set<SearchEntityType> types,
            boolean exactFullText
    ) {
        List<String> typeNames = types.stream().map(Enum::name).toList();
        return new MapSqlParameterSource()
                .addValue("query", query)
                .addValue("likeQuery", escapedLikeQuery)
                .addValue("fullTextQuery", fullTextQuery)
                .addValue("exactFullText", exactFullText)
                .addValue("titleEnabled", fields.contains(SearchField.TITLE))
                .addValue("metadataEnabled", fields.contains(SearchField.METADATA))
                .addValue("fullTextEnabled", fields.contains(SearchField.FULL_TEXT))
                .addValue("types", typeNames);
    }

    private Map<SearchEntityType, Long> zeroEntityCounts() {
        Map<SearchEntityType, Long> counts = new EnumMap<>(SearchEntityType.class);
        for (SearchEntityType type : SearchEntityType.values()) counts.put(type, 0L);
        return counts;
    }
}
