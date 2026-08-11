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
        jdbcTemplate.update("INSERT INTO person_alt_names (person_id, alt_names) VALUES (1, 'Alternativer Testname')");
        jdbcTemplate.update("INSERT INTO origin_places (person_id, place_id) VALUES (1, 1)");
        jdbcTemplate.update("INSERT INTO weaponry (id, original_text, weapon_id, person_id) VALUES (1, 'bewaffnet', 1, 1)");

        jdbcTemplate.update("INSERT INTO citizenship (id, signature, ref_number, date_naturalization, date_misc, entry_text, addendum, person_id, primary_source_id, general_notes, internal_notes) VALUES (1, 'SIG-SEARCH', 'REF-SEARCH', '1501', 'Frühjahr', '<b>seltene Wortfolge</b> Gemeinsam <script>alert</script>', 'Nachtrag Suchbar', 1, 1, 'citizenshippublic', 'citizenshipsecret')");
        jdbcTemplate.update("INSERT INTO citizenship_mentioned_person (citizenship_id, person_id) VALUES (1, 2)");

        searchRepository.refreshSearchDocuments();
    }

    @Test
    void appliesTheSearchSchemaThroughFlyway() {
        Integer successfulMigration = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                  FROM flyway_schema_history
                 WHERE version IN ('1', '2', '3') AND type = 'SQL' AND success
                """, Integer.class);
        assertThat(successfulMigration).isEqualTo(3);
        assertThat(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM global_search_document", Long.class)).isEqualTo(9);
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
            GlobalSearchResponseDTO response = search(query, EnumSet.of(SearchField.TITLE), null, false);
            assertThat(response.content()).extracting(GlobalSearchResultDTO::entityType).contains(expectedType);
        });
    }

    @Test
    void treatsAlternativeAndHistoricalNamesAsRankableTitles() {
        GlobalSearchResponseDTO alternativeName = search(
                "Alternatives Suchgebäude",
                EnumSet.of(SearchField.TITLE),
                EnumSet.of(SearchEntityType.BUILDING),
                false
        );
        GlobalSearchResponseDTO historicalName = search(
                "Historisches Suchhaus",
                EnumSet.of(SearchField.TITLE),
                EnumSet.of(SearchEntityType.BUILDING),
                false
        );

        assertThat(alternativeName.totalElements()).isEqualTo(1);
        assertThat(historicalName.totalElements()).isEqualTo(1);
    }

    @Test
    void searchesCuratedLinkedMetadataButNeverInternalNotes() {
        assertThat(search("Teststraße", EnumSet.of(SearchField.METADATA), EnumSet.of(SearchEntityType.BUILDING), false).totalElements()).isEqualTo(1);
        assertThat(search("Testberuf", EnumSet.of(SearchField.METADATA), EnumSet.of(SearchEntityType.PERSON), false).totalElements()).isEqualTo(1);
        assertThat(search("Erwähnte Testperson", EnumSet.of(SearchField.METADATA), EnumSet.of(SearchEntityType.CITIZENSHIP), false).totalElements()).isEqualTo(1);
        assertThat(search("buildingpublic", EnumSet.of(SearchField.METADATA), EnumSet.of(SearchEntityType.BUILDING), false).totalElements()).isEqualTo(1);
        assertThat(search("buildingsecret", null, null, false).totalElements()).isZero();
        assertThat(search("citizenshipsecret", null, null, false).totalElements()).isZero();
        assertThat(search("auditsecret", null, null, false).totalElements()).isZero();
        assertThat(search("49.123456", null, null, false).totalElements()).isZero();
        assertThat(search("filesecret", null, null, false).totalElements()).isZero();
        assertThat(search("Gemeinsam Testperson", null, EnumSet.of(SearchEntityType.BUILDING), false).totalElements()).isZero();
    }

    @Test
    void supportsSafeFullTextExcerptsAndExactPhrases() {
        GlobalSearchResponseDTO response = search(
                "seltene Wortfolge",
                EnumSet.of(SearchField.FULL_TEXT),
                EnumSet.of(SearchEntityType.CITIZENSHIP),
                true
        );

        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.content().getFirst().excerpt())
                .contains("<mark>seltene</mark>", "<mark>Wortfolge</mark>")
                .doesNotContain("<script>", "</script>");
        assertThat(search("seltene andere", EnumSet.of(SearchField.FULL_TEXT), EnumSet.of(SearchEntityType.CITIZENSHIP), true).totalElements()).isZero();
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
                    false
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
        assertThat(search("\"seltene Wortfolge\"", EnumSet.of(SearchField.FULL_TEXT), EnumSet.of(SearchEntityType.CITIZENSHIP), false).totalElements()).isEqualTo(1);
        assertThat(search("seltene OR unbekannt", EnumSet.of(SearchField.FULL_TEXT), EnumSet.of(SearchEntityType.CITIZENSHIP), false).totalElements()).isEqualTo(1);
        assertThat(search("seltene -Gemeinsam", EnumSet.of(SearchField.FULL_TEXT), EnumSet.of(SearchEntityType.CITIZENSHIP), false).totalElements()).isZero();
    }

    @Test
    void deduplicatesCombinedMatchesAndReturnsFilterAwareFacets() {
        GlobalSearchResponseDTO response = search(
                "Gemeinsam",
                EnumSet.allOf(SearchField.class),
                EnumSet.of(SearchEntityType.PERSON, SearchEntityType.CITIZENSHIP),
                false
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
                "Test",
                EnumSet.of(SearchField.TITLE),
                EnumSet.allOf(SearchEntityType.class),
                false,
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

    private GlobalSearchResponseDTO search(
            String query,
            EnumSet<SearchField> fields,
            EnumSet<SearchEntityType> types,
            boolean exact
    ) {
        return searchService.search(query, fields, types, exact, 0, 20, true);
    }
}
