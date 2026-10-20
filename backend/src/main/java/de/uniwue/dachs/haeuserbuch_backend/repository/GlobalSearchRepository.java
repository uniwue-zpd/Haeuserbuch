package de.uniwue.dachs.haeuserbuch_backend.repository;

import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.GlobalSearchResultDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchEntityType;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchField;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchMatchDTO;
import de.uniwue.dachs.haeuserbuch_backend.search.SearchMatching;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
                       EXISTS (SELECT 1 FROM jsonb_each_text(d.title_details) WHERE value ~* :literalPattern) AS title_literal,
                       (%s AND EXISTS (SELECT 1 FROM jsonb_each_text(d.title_details) WHERE %s)) AS title_match,
                       (%s AND EXISTS (SELECT 1 FROM jsonb_each_text(d.metadata_details) WHERE %s)) AS metadata_match,
                       (d.full_text_vector IS NOT NULL AND (%s)) AS full_text_match,
                       CASE
                           WHEN NOT :fullTextEnabled OR d.full_text_vector IS NULL OR NOT :fuzzy THEN 0.0
                           ELSE ts_rank_cd(d.full_text_vector, websearch_to_tsquery('german', :query))
                       END AS full_text_rank
                  FROM global_search_document d
            ), ranked AS (
                SELECT candidates.*,
                       CASE
                           WHEN :titleEnabled AND title_exact THEN 5
                           WHEN :titleEnabled AND title_literal THEN 4
                           WHEN :titleEnabled AND title_match THEN 3
                           WHEN :metadataEnabled AND metadata_match THEN 2
                           WHEN :fullTextEnabled AND full_text_match THEN 1
                           ELSE 0
                       END AS rank_tier
                  FROM candidates
            )
            """.formatted(
                    SearchMatching.candidates("d.title_vector"), SearchMatching.matches("value"),
                    SearchMatching.candidates("d.metadata_vector"), SearchMatching.matches("value"),
                    SearchMatching.matches(SearchMatching.plainText("d.full_text"), "d.full_text_vector")
            );

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
            Set<SearchField> fields,
            Set<SearchEntityType> types,
            boolean fuzzy,
            int page,
            int size
    ) {
        String sql = CANDIDATES_CTE + """
                SELECT entity_type, entity_id, title, subtitle,
                       title_match, metadata_match, full_text_match,
                       matches.match_fields, matches.match_labels, matches.match_values, matches.match_highlights,
                       CASE WHEN :fullTextEnabled AND full_text_match THEN %s END AS excerpt
                  FROM ranked
                  LEFT JOIN LATERAL (
                      SELECT array_agg(field ORDER BY field, key) AS match_fields,
                             array_agg(key ORDER BY field, key) AS match_labels,
                             array_agg(value ORDER BY field, key) AS match_values,
                             array_agg(%s ORDER BY field, key) AS match_highlights
                        FROM (
                            SELECT 'TITLE' AS field, key, value
                              FROM jsonb_each_text(title_details)
                             WHERE :titleEnabled
                            UNION ALL
                            SELECT 'METADATA', key, value
                              FROM jsonb_each_text(metadata_details)
                             WHERE :metadataEnabled
                        ) details
                       WHERE %s
                  ) matches ON TRUE
                 WHERE entity_type IN (:types)
                   AND """.formatted(
                        SearchMatching.excerpt(SearchMatching.plainText("full_text")),
                        SearchMatching.highlight("value"), SearchMatching.matches("value")) + ENABLED_MATCH + """
                 ORDER BY rank_tier DESC, full_text_rank DESC, LOWER(title), entity_id, entity_type
                 LIMIT :limit OFFSET :offset
                """;

        MapSqlParameterSource parameters = parameters(query, fields, types, fuzzy)
                .addValue("limit", size)
                .addValue("offset", (long) page * size);

        return jdbcTemplate.query(sql, parameters, (resultSet, rowNumber) -> {
            Set<SearchField> matchedFields = new LinkedHashSet<>();
            if (fields.contains(SearchField.TITLE) && resultSet.getBoolean("title_match")) matchedFields.add(SearchField.TITLE);
            if (fields.contains(SearchField.METADATA) && resultSet.getBoolean("metadata_match")) matchedFields.add(SearchField.METADATA);
            if (fields.contains(SearchField.FULL_TEXT) && resultSet.getBoolean("full_text_match")) matchedFields.add(SearchField.FULL_TEXT);
            List<SearchMatchDTO> matches = new ArrayList<>();
            if (resultSet.getArray("match_fields") != null) {
                String[] matchFields = (String[]) resultSet.getArray("match_fields").getArray();
                String[] matchLabels = (String[]) resultSet.getArray("match_labels").getArray();
                String[] matchValues = (String[]) resultSet.getArray("match_values").getArray();
                String[] matchHighlights = (String[]) resultSet.getArray("match_highlights").getArray();
                for (int i = 0; i < matchFields.length; i++) {
                    matches.add(new SearchMatchDTO(SearchField.valueOf(matchFields[i]), matchLabels[i], matchValues[i], matchHighlights[i]));
                }
            }
            return new GlobalSearchResultDTO(
                    SearchEntityType.valueOf(resultSet.getString("entity_type")),
                    resultSet.getLong("entity_id"),
                    resultSet.getString("title"),
                    resultSet.getString("subtitle"),
                    matchedFields,
                    resultSet.getString("excerpt"),
                    matches
            );
        });
    }

    public long count(
            String query,
            Set<SearchField> fields,
            Set<SearchEntityType> types,
            boolean fuzzy
    ) {
        String sql = CANDIDATES_CTE + """
                SELECT COUNT(*)
                  FROM ranked
                 WHERE entity_type IN (:types)
                   AND """ + ENABLED_MATCH;
        Long count = jdbcTemplate.queryForObject(
                sql,
                parameters(query, fields, types, fuzzy),
                Long.class
        );
        return count == null ? 0 : count;
    }

    public Map<SearchEntityType, Long> countByEntityType(
            String query,
            Set<SearchField> fields,
            Set<SearchEntityType> types,
            boolean fuzzy
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
                parameters(query, fields, types, fuzzy),
                (RowCallbackHandler) resultSet -> counts.put(
                        SearchEntityType.valueOf(resultSet.getString("entity_type")),
                        resultSet.getLong("result_count")
                )
        );
        return counts;
    }

    public Map<SearchField, Long> countByField(
            String query,
            Set<SearchField> fields,
            Set<SearchEntityType> types,
            boolean fuzzy
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
                parameters(query, fields, types, fuzzy),
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
            Set<SearchField> fields,
            Set<SearchEntityType> types,
            boolean fuzzy
    ) {
        List<String> typeNames = types.stream().map(Enum::name).toList();
        return new MapSqlParameterSource()
                .addValue("query", query)
                .addValue("fuzzy", fuzzy)
                .addValue("literalPattern", SearchMatching.literalPattern(query))
                .addValue("highlightPattern", SearchMatching.highlightPattern(query))
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
