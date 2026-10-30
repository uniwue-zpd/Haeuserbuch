package de.uniwue.dachs.haeuserbuch_backend;

import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.GlobalSearchResponseDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.GlobalSearchResultDTO;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchEntityType;
import de.uniwue.dachs.haeuserbuch_backend.DTO.Search.SearchField;
import de.uniwue.dachs.haeuserbuch_backend.repository.GlobalSearchRepository;
import de.uniwue.dachs.haeuserbuch_backend.search.GlobalSearchRefreshCoordinator;
import de.uniwue.dachs.haeuserbuch_backend.service.GlobalSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.EnumSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class GlobalSearchIntegrationTests {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("imresamu/postgis:17-3.5-alpine").asCompatibleSubstituteFor("postgres")
    ).withInitScript("db/enable-postgis.sql");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private GlobalSearchRefreshCoordinator refreshCoordinator;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private GlobalSearchService searchService;

    @Autowired
    private GlobalSearchRepository searchRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void createSearchFixtures() {
        jdbcTemplate.execute("TRUNCATE TABLE citizenship, person, building, place, source, street, district, quarter, job, religion, weapon, file CASCADE");

        jdbcTemplate.update("INSERT INTO district (id, name, description, general_notes, internal_notes) VALUES (1, 'Testdistrikt', 'Distriktbeschreibung', 'distriktpublic', 'distriktsecret')");
        jdbcTemplate.update("INSERT INTO quarter (id, name, description, general_notes) VALUES (1, 'Testviertel', 'Viertelbeschreibung', 'viertelpublic')");
        jdbcTemplate.update("INSERT INTO street (id, name, description, general_notes) VALUES (1, 'Teststraße', 'Straßenbeschreibung', 'streetpublic')");
        jdbcTemplate.update("INSERT INTO street_alt_names (street_id, alt_names) VALUES (1, 'Alte Testgasse')");
        jdbcTemplate.update("INSERT INTO source (id, title, type, signature, description, general_notes, internal_notes) VALUES (1, 'Testquelle', 'Archiv', 'Q-1', 'Quellenbeschreibung', 'sourcepublic', 'sourcesecret')");
        jdbcTemplate.update("INSERT INTO source_authors (source_id, authors) VALUES (1, 'Autor Suchbar')");

        jdbcTemplate.update("INSERT INTO building (id, district_property_number, property_number, object, year, part_type, district_id, quarter_id, general_notes, internal_notes, created_by, coordinates) VALUES (1, 'HAUS-1', 'P-1', 'Suchhaus', 1550, 'Haupthaus', 1, 1, 'buildingpublic', 'buildingsecret', 'auditsecret', ST_GeomFromText('POINT(9.876543 49.123456)', 4326))");
        jdbcTemplate.update("INSERT INTO building_name (id, name, from_date, building_id) VALUES (1, 'Historisches Suchhaus', '1500', 1)");
        jdbcTemplate.update("INSERT INTO building_alt_names (building_id, alt_names) VALUES (1, 'Alternatives Suchgebäude')");
        jdbcTemplate.update("INSERT INTO address (id, house_number, street_id, building_id) VALUES (1, '7', 1, 1)");
        jdbcTemplate.update("INSERT INTO building_source (building_id, source_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO file (id, name, original_name, path, size, type) VALUES (1, 'stored-name', 'filesecret.pdf', '/uploads/filesecret.pdf', 42, 'application/pdf')");
        jdbcTemplate.update("INSERT INTO building_file (building_id, file_id) VALUES (1, 1)");

        jdbcTemplate.update("INSERT INTO place (id, real_name, is_uncertain, general_notes, internal_notes) VALUES (1, 'Testort', false, 'placepublic', 'placesecret')");
        jdbcTemplate.update("INSERT INTO place_alt_names (place_id, alt_names) VALUES (1, 'Alter Testort')");
        jdbcTemplate.update("INSERT INTO job (id, name) VALUES (1, 'Testberuf')");
        jdbcTemplate.update("INSERT INTO religion (id, name) VALUES (1, 'Testreligion')");
        jdbcTemplate.update("INSERT INTO weapon (id, name) VALUES (1, 'Testwaffe')");
        jdbcTemplate.update("INSERT INTO person (id, first_name, last_name, full_name, sex, origin_original_text, job_original_text, religion_original_text, building_id, job_category_id, religion_category_id, general_notes, internal_notes) VALUES (1, 'Gemeinsam', 'Person', 'Gemeinsam Testperson', 'männlich', 'aus dem Testland', 'Handwerk', 'Bekenntnis', 1, 1, 1, 'Gemeinsam personpublic', 'personsecret')");
        jdbcTemplate.update("INSERT INTO person (id, full_name) VALUES (2, 'Erwähnte Testperson')");
        jdbcTemplate.update("INSERT INTO person (id, full_name) VALUES (3, 'Bürgender Garant')");
        jdbcTemplate.update("INSERT INTO person_alt_names (person_id, alt_names) VALUES (1, 'Alternativer Testname')");
        jdbcTemplate.update("INSERT INTO origin_places (person_id, place_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO weaponry (id, original_text, weapon_id, person_id) VALUES (1, 'bewaffnet', 1, 1)");

        jdbcTemplate.update("INSERT INTO citizenship (id, signature, ref_number, date_naturalization, date_misc, entry_text, addendum, person_id, primary_source_id, general_notes, internal_notes) VALUES (1, 'SIG-SEARCH', 'REF-SEARCH', '1501', 'Frühjahr', '<b>seltene Wortfolge</b> Gemeinsam <script>alert</script>', 'Nachtrag Suchbar', 1, 1, 'citizenshippublic', 'citizenshipsecret')");
        jdbcTemplate.update("INSERT INTO citizenship_mentioned_person (citizenship_id, person_id) VALUES (1, 2)");
        jdbcTemplate.update("INSERT INTO citizenship_guarantor (citizenship_id, person_id) VALUES (1, 3)");

        searchRepository.refreshSearchDocuments();
    }

    @Test
    void appliesTheSearchSchemaThroughFlyway() {
        Integer successfulMigration = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                  FROM flyway_schema_history
                 WHERE version IN ('1', '2', '3', '6', '7', '8', '9') AND type = 'SQL' AND success
                """, Integer.class);
        assertThat(successfulMigration).isEqualTo(7);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM global_search_document", Long.class)).isEqualTo(10);
    }

    @Test
    void searchesTitlesAcrossEveryPublicEntityType() {
        Map<String, SearchEntityType> cases = Map.of(
                "Suchhaus", SearchEntityType.BUILDING,
                "Testperson", SearchEntityType.PERSON,
                "Testort", SearchEntityType.PLACE,
                "SIG-SEARCH", SearchEntityType.CITIZENSHIP,
                "Testquelle", SearchEntityType.SOURCE,
                "Teststraße", SearchEntityType.STREET,
                "Testdistrikt", SearchEntityType.DISTRICT,
                "Testviertel", SearchEntityType.QUARTER
        );

        cases.forEach((query, expectedType) -> {
            GlobalSearchResponseDTO response = search(query, EnumSet.of(SearchField.TITLE), null, true);
            assertThat(response.content()).extracting(GlobalSearchResultDTO::entityType).contains(expectedType);
        });
    }

    @Test
    void treatsAlternativeAndHistoricalNamesAsRankableTitles() {
        GlobalSearchResponseDTO alternativeName = search(
                "Alternatives Suchgebäude",
                EnumSet.of(SearchField.TITLE),
                EnumSet.of(SearchEntityType.BUILDING),
                true
        );
        GlobalSearchResponseDTO historicalName = search(
                "Historisches Suchhaus",
                EnumSet.of(SearchField.TITLE),
                EnumSet.of(SearchEntityType.BUILDING),
                true
        );

        assertThat(alternativeName.totalElements()).isEqualTo(1);
        assertThat(historicalName.totalElements()).isEqualTo(1);
    }

    @Test
    void searchesCuratedLinkedMetadataButNeverInternalNotes() {
        assertThat(search("Teststraße", EnumSet.of(SearchField.METADATA), EnumSet.of(SearchEntityType.BUILDING), true).totalElements()).isEqualTo(1);
        assertThat(search("Testberuf", EnumSet.of(SearchField.METADATA), EnumSet.of(SearchEntityType.PERSON), true).totalElements()).isEqualTo(1);
        assertThat(search("Erwähnte Testperson", EnumSet.of(SearchField.METADATA), EnumSet.of(SearchEntityType.CITIZENSHIP), true).totalElements()).isEqualTo(1);
        assertThat(search("Bürgender Garant", EnumSet.of(SearchField.METADATA), EnumSet.of(SearchEntityType.CITIZENSHIP), true).totalElements()).isEqualTo(1);
        assertThat(search("buildingpublic", EnumSet.of(SearchField.METADATA), EnumSet.of(SearchEntityType.BUILDING), true).totalElements()).isEqualTo(1);
        assertThat(search("buildingsecret", null, null, true).totalElements()).isZero();
        assertThat(search("citizenshipsecret", null, null, true).totalElements()).isZero();
        assertThat(search("auditsecret", null, null, true).totalElements()).isZero();
        assertThat(search("49.123456", null, null, true).totalElements()).isZero();
        assertThat(search("filesecret", null, null, true).totalElements()).isZero();
        assertThat(search("Gemeinsam Testperson", null, EnumSet.of(SearchEntityType.BUILDING), true).totalElements()).isZero();
    }

    @Test
    void exposesSpecificTitleAndMetadataValuesForHighlighting() {
        var title = search("alternatives", EnumSet.of(SearchField.TITLE), EnumSet.of(SearchEntityType.BUILDING), true)
                .content().getFirst();
        assertThat(title.matches()).anySatisfy(match -> {
            assertThat(match.field()).isEqualTo(SearchField.TITLE);
            assertThat(match.label()).isEqualTo("Alternativnamen");
            assertThat(match.value()).isEqualTo("Alternatives Suchgebäude");
        });
        var metadata = search("Testberuf", EnumSet.of(SearchField.METADATA), EnumSet.of(SearchEntityType.PERSON), true)
                .content().getFirst();
        assertThat(metadata.matches()).singleElement().satisfies(match -> {
            assertThat(match.field()).isEqualTo(SearchField.METADATA);
            assertThat(match.label()).isEqualTo("Beruf");
            assertThat(match.value()).isEqualTo("Testberuf");
        });
        var combined = search("Gemeinsam", null, EnumSet.of(SearchEntityType.PERSON), true).content().getFirst();
        assertThat(combined.matches()).extracting(match -> match.field()).contains(SearchField.TITLE, SearchField.METADATA);
        var titleOnly = search("Gemeinsam", EnumSet.of(SearchField.TITLE), EnumSet.of(SearchEntityType.PERSON), true)
                .content().getFirst();
        assertThat(titleOnly.matches()).allSatisfy(match -> assertThat(match.field()).isEqualTo(SearchField.TITLE));
    }

    @Test
    void supportsSafeFullTextExcerptsAndExactPhrases() {
        GlobalSearchResponseDTO response = search(
                "seltene Wortfolge",
                EnumSet.of(SearchField.FULL_TEXT),
                EnumSet.of(SearchEntityType.CITIZENSHIP),
                false
        );

        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.content().getFirst().excerpt())
                .contains("<mark>seltene Wortfolge</mark>")
                .doesNotContain("<script>", "</script>");
        assertThat(search("seltene andere", EnumSet.of(SearchField.FULL_TEXT), EnumSet.of(SearchEntityType.CITIZENSHIP), false).totalElements()).isZero();
    }

    @Test
    void enablesFullTextForEveryDocumentThatProvidesAFullTextVector() {
        GlobalSearchResponseDTO response = new TransactionTemplate(transactionManager).execute(status -> {
            jdbcTemplate.execute("DROP MATERIALIZED VIEW global_search_document");
            jdbcTemplate.execute("""
                    CREATE TABLE global_search_document (
                        entity_type text,
                        entity_id bigint,
                        title text,
                        subtitle text,
                        title_values text[],
                        title_text text,
                        metadata_text text,
                        title_details jsonb DEFAULT '{}',
                        title_vector tsvector,
                        metadata_vector tsvector,
                        metadata_details jsonb DEFAULT '{}',
                        full_text text,
                        full_text_vector tsvector
                    )
                    """);
            jdbcTemplate.update("""
                    INSERT INTO global_search_document (
                        entity_type, entity_id, title, title_values, title_text,
                        metadata_text, full_text, full_text_vector
                    ) VALUES (
                        'PERSON', 99, 'Volltextperson', ARRAY['Volltextperson'], 'Volltextperson',
                        '', 'generischer Volltext für neue Seitentypen',
                        to_tsvector('german', 'generischer Volltext für neue Seitentypen')
                    )
                    """);

            GlobalSearchResponseDTO result = search(
                    "generischer",
                    EnumSet.of(SearchField.FULL_TEXT),
                    EnumSet.of(SearchEntityType.PERSON),
                    true
            );
            status.setRollbackOnly();
            return result;
        });

        assertThat(response).isNotNull();
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.content().getFirst().entityType()).isEqualTo(SearchEntityType.PERSON);
        assertThat(response.content().getFirst().matchedFields()).containsExactly(SearchField.FULL_TEXT);
    }

    @Test
    void supportsBooleanPhraseAndExclusionFullTextSyntax() {
        assertThat(search("\"seltene Wortfolge\"", EnumSet.of(SearchField.FULL_TEXT), EnumSet.of(SearchEntityType.CITIZENSHIP), true).totalElements()).isEqualTo(1);
        assertThat(search("seltene OR unbekannt", EnumSet.of(SearchField.FULL_TEXT), EnumSet.of(SearchEntityType.CITIZENSHIP), true).totalElements()).isEqualTo(1);
        assertThat(search("seltene -Gemeinsam", EnumSet.of(SearchField.FULL_TEXT), EnumSet.of(SearchEntityType.CITIZENSHIP), true).totalElements()).isZero();
    }

    @Test
    void deduplicatesCombinedMatchesAndReturnsFilterAwareFacets() {
        GlobalSearchResponseDTO response = search(
                "Gemeinsam",
                EnumSet.allOf(SearchField.class),
                EnumSet.of(SearchEntityType.PERSON, SearchEntityType.CITIZENSHIP),
                true
        );

        assertThat(response.totalElements()).isEqualTo(2);
        GlobalSearchResultDTO person = response.content().stream()
                .filter(result -> result.entityType() == SearchEntityType.PERSON)
                .findFirst()
                .orElseThrow();
        assertThat(person.matchedFields()).containsExactly(SearchField.TITLE, SearchField.METADATA);
        assertThat(response.content().getFirst().entityType()).isEqualTo(SearchEntityType.PERSON);
        assertThat(response.facets().entityTypes().get(SearchEntityType.PERSON)).isEqualTo(1);
        assertThat(response.facets().fields().get(SearchField.FULL_TEXT)).isEqualTo(1);
    }

    @Test
    void paginatesWithDeterministicTotals() {
        GlobalSearchResponseDTO firstPage = searchService.search(
                "Testperson OR Testquelle",
                EnumSet.of(SearchField.TITLE),
                EnumSet.allOf(SearchEntityType.class),
                true,
                0,
                1,
                false
        );

        assertThat(firstPage.content()).hasSize(1);
        assertThat(firstPage.totalElements()).isGreaterThan(1);
        assertThat(firstPage.totalPages()).isEqualTo(firstPage.totalElements());
        assertThat(firstPage.facets()).isNull();
    }

    @Test
    void validatesQueryAndPaginationBounds() {
        assertThatThrownBy(() -> searchService.search("x", null, null, false, 0, 20, true)).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> searchService.search("valid", null, null, false, -1, 20, true)).isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> searchService.search("valid", null, null, false, 0, 51, true)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void exposesAnonymousGetAndValidatesControllerParameters() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "Testquelle")
                        .param("fields", "TITLE")
                        .param("types", "SOURCE")
                        .param("includeFacets", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.facets").doesNotExist());
        mockMvc.perform(get("/search").param("query", "x"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/search").param("query", "x".repeat(201)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/search").param("query", "valid").param("fields", "UNKNOWN"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/search").param("query", "valid").param("types", "UNKNOWN"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/search").param("query", "valid").param("size", "51"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/search").param("query", "does-not-exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void protectsTheManualRefreshEndpointForMaintenanceRoles() throws Exception {
        mockMvc.perform(post("/search/refresh"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/search/refresh")
                        .with(jwt().authorities(createAuthorityList("ROLE_editor"))))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/search/refresh")
                        .with(jwt().authorities(createAuthorityList("ROLE_admin"))))
                .andExpect(status().isAccepted());

        verify(refreshCoordinator).requestRefresh();
    }

    @Test
    void findsInflectedFormsAcrossAllFieldsAndExplainsMetadataHits() {
        jdbcTemplate.update("UPDATE building SET object = 'goldenen Gans', general_notes = 'goldenen Gans' WHERE id = 1");
        jdbcTemplate.update("UPDATE citizenship SET entry_text = '<b>goldenen Gans</b>' WHERE id = 1");
        searchRepository.refreshSearchDocuments();

        for (SearchField field : SearchField.values()) {
            var types = EnumSet.of(field == SearchField.FULL_TEXT ? SearchEntityType.CITIZENSHIP : SearchEntityType.BUILDING);
            var fuzzy = search("goldene Gans", EnumSet.of(field), types, true);
            assertThat(fuzzy.totalElements()).as(field.name()).isEqualTo(1);
            assertThat(fuzzy.facets().fields().get(field)).isEqualTo(1);
            assertThat(fuzzy.content().getFirst().matchedFields()).containsExactly(field);
            if (field != SearchField.FULL_TEXT) {
                assertThat(fuzzy.content().getFirst().matches()).anySatisfy(match -> {
                    assertThat(match.value()).isEqualTo("goldenen Gans");
                    assertThat(match.highlightedValue()).contains("<mark>goldenen</mark>", "<mark>Gans</mark>");
                });
            }
            assertThat(search("goldene Gans", EnumSet.of(field), types, false).totalElements()).isZero();
            assertThat(search("GOLDENEN GANS", EnumSet.of(field), types, false).totalElements()).isEqualTo(1);
        }
    }

    @Test
    void searchesPersonEmigrationTextAndPlaceAsMetadata() {
        jdbcTemplate.update("UPDATE person SET emigration_original_text = 'nach Wegzugshausen', emigration_place_id = 1 WHERE id = 1");
        searchRepository.refreshSearchDocuments();
        var fields = EnumSet.of(SearchField.METADATA);
        var types = EnumSet.of(SearchEntityType.PERSON);
        var emigrationText = search("Wegzugshausen", fields, types, true);
        assertThat(emigrationText.totalElements()).isEqualTo(1);
        assertThat(emigrationText.content().getFirst().matches())
                .anySatisfy(match -> assertThat(match.label()).isEqualTo("Wegzugsort (Originaltext)"));
        assertThat(search("Testort", fields, types, true).content().getFirst().matches())
                .anySatisfy(match -> assertThat(match.label()).isEqualTo("Wegzugsort"));
    }

    @Test
    void matchesEachMetadataValueIndependentlyAndSupportsWordSearchSyntax() {
        jdbcTemplate.update("UPDATE source SET description = 'goldenen Gans', general_notes = 'Silber' WHERE id = 1");
        searchRepository.refreshSearchDocuments();
        var fields = EnumSet.of(SearchField.METADATA);
        var types = EnumSet.of(SearchEntityType.SOURCE);
        for (String query : new String[]{"goldene Gans", "Gans goldene", "goldene OR Ente", "goldene -Ente", "goldene -Silber", "-Silber", "goldene OR -Silber", "\"goldene Gans\""}) {
            var response = search(query, fields, types, true);
            assertThat(response.totalElements()).as(query).isEqualTo(1);
            assertThat(response.content().getFirst().matches()).allSatisfy(match -> assertThat(match.value()).isNotBlank());
        }
        for (String query : new String[]{"goldene -Gans", "goldene Silber", "\"Gans goldene\"", "gol", "der und"}) {
            assertThat(search(query, fields, types, true).totalElements()).as(query).isZero();
        }
        assertThat(search("goldenen Gans Silber", fields, types, false).totalElements()).isZero();
        assertThat(search("gold", fields, types, false).totalElements()).isZero();
        jdbcTemplate.update("UPDATE building SET object = 'goldene', property_number = 'Gans' WHERE id = 1");
        searchRepository.refreshSearchDocuments();
        assertThat(search("goldene Gans", EnumSet.of(SearchField.TITLE), EnumSet.of(SearchEntityType.BUILDING), true).totalElements()).isZero();
    }

    @Test
    void treatsPunctuationLiterallyAndEscapesHighlightsInBothModes() {
        jdbcTemplate.update("UPDATE source SET description = 'Haus (A+B). & Gans <img src=x onerror=alert(1)>', general_notes = 'der und' WHERE id = 1");
        searchRepository.refreshSearchDocuments();
        var fields = EnumSet.of(SearchField.METADATA);
        var types = EnumSet.of(SearchEntityType.SOURCE);
        for (String query : new String[]{"Haus (A+B).", "(A+B).", "& Gans", "der und"}) {
            assertThat(search(query, fields, types, false).totalElements()).as(query).isEqualTo(1);
        }
        assertThat(search("Haus (A.B).", fields, types, false).totalElements()).isZero();
        for (boolean fuzzy : new boolean[]{true, false}) {
            var result = search("Gans", fields, types, fuzzy).content().getFirst();
            assertThat(result.matches().getFirst().highlightedValue()).contains("<mark>Gans</mark>", "&lt;").doesNotContain("<img");
        }
        var ampersand = search("& Gans", fields, types, false).content().getFirst();
        assertThat(ampersand.matches().getFirst().highlightedValue()).contains("<mark>&amp; Gans</mark>");
    }

    @Test
    void ranksLiteralTitlesBeforeStemmedTitlesAndMetadata() {
        jdbcTemplate.update("UPDATE source SET title = 'goldene Gans' WHERE id = 1");
        jdbcTemplate.update("UPDATE place SET real_name = 'goldenen Gans' WHERE id = 1");
        jdbcTemplate.update("UPDATE building SET general_notes = 'goldenen Gans' WHERE id = 1");
        searchRepository.refreshSearchDocuments();
        var response = search("goldene Gans", null,
                EnumSet.of(SearchEntityType.SOURCE, SearchEntityType.PLACE, SearchEntityType.BUILDING), true);
        assertThat(response.content()).extracting(GlobalSearchResultDTO::entityType)
                .containsExactly(SearchEntityType.SOURCE, SearchEntityType.PLACE, SearchEntityType.BUILDING);
    }

    @Test
    void resolvesFuzzyDefaultsAndLegacyAliasesForBothEndpoints() throws Exception {
        jdbcTemplate.update("UPDATE source SET description = 'goldenen Gans' WHERE id = 1");
        jdbcTemplate.update("UPDATE citizenship SET entry_text = '<b>goldenen Gans</b>' WHERE id = 1");
        searchRepository.refreshSearchDocuments();
        for (String endpoint : new String[]{"/search", "/citizenships/fulltextsearch"}) {
            String legacy = endpoint.equals("/search") ? "exactFullText" : "exact";
            mockMvc.perform(get(endpoint).param("query", "goldene Gans"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(endpoint.equals("/search") ? 2 : 1));
            mockMvc.perform(get(endpoint).param("query", "goldene Gans").param(legacy, "true"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
            mockMvc.perform(get(endpoint).param("query", "goldene Gans").param("fuzzy", "true").param(legacy, "true"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(endpoint.equals("/search") ? 2 : 1));
            mockMvc.perform(get(endpoint).param("query", "goldene Gans").param("fuzzy", "false").param(legacy, "false"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
            mockMvc.perform(get(endpoint).param("query", "GOLDENEN GANS").param("fuzzy", "false"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(endpoint.equals("/search") ? 2 : 1));
        }
    }

    @Test
    void filtersCitizenshipsByGuarantorSeparatelyFromMentionedPersons() throws Exception {
        mockMvc.perform(get("/citizenships").param("guarantor", "bürgender"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].guarantors[0].fullName").value("Bürgender Garant"))
                .andExpect(jsonPath("$.content[0].mentionedPersons[0].fullName").value("Erwähnte Testperson"));
        mockMvc.perform(get("/citizenships").param("guarantor", "Erwähnte"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
        mockMvc.perform(get("/citizenships/filter").param("guarantor-id", "3"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/citizenships/filter").param("guarantor-id", "2"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void dedicatedFullTextSearchUsesLiteralPunctuationAndSafeHtml() throws Exception {
        jdbcTemplate.update("UPDATE citizenship SET entry_text = '<b>Haus (A+B).</b> & Gans <script>alert(1)</script>' WHERE id = 1");
        mockMvc.perform(get("/citizenships/fulltextsearch").param("query", "Haus (A+B).").param("fuzzy", "false"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].queryResult").value(org.hamcrest.Matchers.containsString("<mark>Haus (A+B).</mark>")))
                .andExpect(jsonPath("$.content[0].queryResult").value(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("<script>"))));
        mockMvc.perform(get("/citizenships/fulltextsearch").param("query", "Haus (A.B).").param("fuzzy", "false"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void strictFullTextMatchesRenderedPhrasesAcrossInlineAndBlockMarkup() throws Exception {
        for (String text : new String[]{
                "<p>goldene <b>Gans</b></p>",
                "<p>gol<b>dene</b> Gans</p>",
                "<p>goldene</p><p>Gans</p>",
                "<p>goldene<br>Gans</p>"
        }) {
            jdbcTemplate.update("UPDATE citizenship SET entry_text = ? WHERE id = 1", text);
            searchRepository.refreshSearchDocuments();
            var response = search("goldene Gans", EnumSet.of(SearchField.FULL_TEXT),
                    EnumSet.of(SearchEntityType.CITIZENSHIP), false);
            assertThat(response.totalElements()).as(text).isEqualTo(1);
            assertThat(response.content().getFirst().excerpt()).contains("<mark>goldene Gans</mark>");
            assertThat(search("goldeneGans", EnumSet.of(SearchField.FULL_TEXT),
                    EnumSet.of(SearchEntityType.CITIZENSHIP), false).totalElements()).isZero();
            mockMvc.perform(get("/citizenships/fulltextsearch").param("query", "goldene Gans").param("fuzzy", "false"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.content[0].queryResult").value(org.hamcrest.Matchers.containsString("<mark>goldene Gans</mark>")));
        }
    }

    @Test
    void strictExcerptsIncludeLateMatchesRegardlessOfCase() {
        jdbcTemplate.update("UPDATE citizenship SET entry_text = ? WHERE id = 1",
                "padding ".repeat(100) + "GOLDENE GANS");
        searchRepository.refreshSearchDocuments();
        var response = search("goldene Gans", EnumSet.of(SearchField.FULL_TEXT),
                EnumSet.of(SearchEntityType.CITIZENSHIP), false);
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.content().getFirst().excerpt()).contains("<mark>GOLDENE GANS</mark>").hasSizeLessThan(600);
    }

    private GlobalSearchResponseDTO search(
            String query,
            EnumSet<SearchField> fields,
            EnumSet<SearchEntityType> types,
            boolean fuzzy
    ) {
        return searchService.search(query, fields, types, fuzzy, 0, 20, true);
    }
}
