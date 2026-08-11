package de.uniwue.dachs.haeuserbuch_backend;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class FlywayBaselineContractTests {
    private static final Pattern APPLICATION_TABLE = Pattern.compile(
            "(?m)^CREATE TABLE (public\\.[a-z0-9_]+)"
    );

    @Test
    void v1ContainsExactlyTheApplicationTablesFromTheLegacyDump() throws IOException {
        Set<String> expectedTables = new TreeSet<>(resource("/db/legacy-application-tables.txt")
                .lines()
                .filter(line -> !line.isBlank())
                .toList());
        String baseline = resource("/db/migration/V1__baseline_schema.sql");

        assertThat(applicationTables(baseline)).isEqualTo(expectedTables);
    }

    private static Set<String> applicationTables(String sql) {
        Set<String> tables = new TreeSet<>();
        var matcher = APPLICATION_TABLE.matcher(sql);
        while (matcher.find()) {
            tables.add(matcher.group(1));
        }
        return tables;
    }

    private static String resource(String path) throws IOException {
        try (InputStream input = FlywayBaselineContractTests.class.getResourceAsStream(path)) {
            if (input == null) {
                throw new IllegalStateException("Could not locate classpath resource " + path);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
