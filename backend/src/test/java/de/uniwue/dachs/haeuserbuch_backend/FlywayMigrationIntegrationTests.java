package de.uniwue.dachs.haeuserbuch_backend;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class FlywayMigrationIntegrationTests {
    private static final String EMPTY_DATABASE = "flyway_empty_path";
    private static final String LEGACY_DATABASE = "flyway_legacy_path";

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("imresamu/postgis:17-3.5-alpine").asCompatibleSubstituteFor("postgres")
    );

    @Test
    void migratesEmptyAndLegacyDatabasesToTheSameCurrentSchema() throws SQLException {
        createDatabase(EMPTY_DATABASE);
        createDatabase(LEGACY_DATABASE);
        enablePostgis(EMPTY_DATABASE);
        enablePostgis(LEGACY_DATABASE);

        Flyway emptyFlyway = flyway(EMPTY_DATABASE, false, null);
        emptyFlyway.migrate();

        assertThat(migrationHistory(EMPTY_DATABASE))
                .containsExactly("1:SQL", "2:SQL", "3:SQL");
        assertCurrentSchema(EMPTY_DATABASE);

        Flyway legacySeed = flyway(LEGACY_DATABASE, false, MigrationVersion.fromVersion("1"));
        legacySeed.migrate();
        execute(LEGACY_DATABASE, "DROP TABLE public.flyway_schema_history");

        Flyway legacyFlyway = flyway(LEGACY_DATABASE, true, null);
        legacyFlyway.migrate();

        assertThat(migrationHistory(LEGACY_DATABASE))
                .containsExactly("1:BASELINE", "2:SQL", "3:SQL");
        assertCurrentSchema(LEGACY_DATABASE);
    }

    private static Flyway flyway(String database, boolean baselineOnMigrate, MigrationVersion target) {
        var configuration = Flyway.configure()
                .dataSource(jdbcUrl(database), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .defaultSchema("public")
                .schemas("public")
                .baselineOnMigrate(baselineOnMigrate)
                .baselineVersion(MigrationVersion.fromVersion("1"))
                .cleanDisabled(true)
                .validateMigrationNaming(true)
                .validateOnMigrate(true);
        if (target != null) {
            configuration.target(target);
        }
        return configuration.load();
    }

    private static void createDatabase(String database) throws SQLException {
        execute(POSTGRES.getDatabaseName(), "CREATE DATABASE " + database);
    }

    private static void enablePostgis(String database) throws SQLException {
        execute(database, "CREATE EXTENSION postgis WITH SCHEMA public");
    }

    private static void assertCurrentSchema(String database) throws SQLException {
        try (Connection connection = connection(database);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("""
                     SELECT to_regclass('public.file')::text,
                            to_regclass('public.building_file')::text,
                            to_regclass('public.global_search_document')::text
                     """)) {
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getString(1)).isEqualTo("file");
            assertThat(resultSet.getString(2)).isEqualTo("building_file");
            assertThat(resultSet.getString(3)).isEqualTo("global_search_document");
        }
    }

    private static List<String> migrationHistory(String database) throws SQLException {
        List<String> migrations = new ArrayList<>();
        try (Connection connection = connection(database);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("""
                     SELECT version, type
                       FROM public.flyway_schema_history
                      WHERE success
                      ORDER BY installed_rank
                     """)) {
            while (resultSet.next()) {
                migrations.add(resultSet.getString("version") + ":" + resultSet.getString("type"));
            }
        }
        return migrations;
    }

    private static void execute(String database, String sql) throws SQLException {
        try (Connection connection = connection(database);
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static Connection connection(String database) throws SQLException {
        return DriverManager.getConnection(jdbcUrl(database), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    private static String jdbcUrl(String database) {
        return POSTGRES.getJdbcUrl().replace(
                "/" + POSTGRES.getDatabaseName(),
                "/" + database
        );
    }
}
