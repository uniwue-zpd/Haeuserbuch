package de.uniwue.dachs.haeuserbuch_backend.search;

/** Shared matching and safe highlighting for both search entry points. */
public final class SearchMatching {
    private SearchMatching() {}

    private static final String PLAIN_TEXT_PREFIX =
            "btrim(regexp_replace(regexp_replace(regexp_replace(COALESCE(";
    private static final String PLAIN_TEXT_SUFFIX =
            ", ''), '</?(?:p|div|br|h[1-6]|li|ul|ol|blockquote|pre|table|tr|td|th|hr|section|article)\\y[^>]*>', ' ', 'gi'), "
                    + "'<[^>]*>', '', 'g'), '[[:space:]]+', ' ', 'g'))";
    private static final String HTML_ESCAPE_PREFIX = "REPLACE(REPLACE(REPLACE(COALESCE(";
    private static final String HTML_ESCAPE_SUFFIX = ", ''), '&', '&amp;'), '<', '&lt;'), '>', '&gt;')";

    // Spring Data @Query annotations require compile-time constants. Compose them from
    // the same fragments as the dynamic global-search SQL to keep both paths consistent.
    public static final String CITIZENSHIP_TEXT = PLAIN_TEXT_PREFIX + "cz.entry_text" + PLAIN_TEXT_SUFFIX;
    public static final String CITIZENSHIP_MATCH =
            "CASE WHEN :fuzzy THEN cz.full_text_vector @@ websearch_to_tsquery('german', :query) "
                    + "ELSE " + CITIZENSHIP_TEXT + " ~* :literalPattern END";
    private static final String CITIZENSHIP_ESCAPED =
            HTML_ESCAPE_PREFIX + CITIZENSHIP_TEXT + HTML_ESCAPE_SUFFIX;
    public static final String CITIZENSHIP_HIGHLIGHT =
            "CASE WHEN :fuzzy THEN ts_headline('german', " + CITIZENSHIP_ESCAPED
                    + ", websearch_to_tsquery('german', :query), 'StartSel=<mark>, StopSel=</mark>, HighlightAll=true') "
                    + "ELSE regexp_replace(" + CITIZENSHIP_ESCAPED + ", :highlightPattern, '<mark>\\1</mark>', 'gi') END";

    public static boolean resolveFuzzy(Boolean fuzzy, boolean exact) {
        return fuzzy != null ? fuzzy : !exact;
    }

    /** PostgreSQL ARE escaping; Java's Pattern.quote is not valid PostgreSQL syntax. */
    public static String literalPattern(String query) {
        StringBuilder escaped = new StringBuilder();
        query.codePoints().forEach(character -> {
            if ("\\.^$|?*+()[]{}".indexOf(character) >= 0) escaped.append('\\');
            escaped.appendCodePoint(character);
        });
        // Lookarounds also support phrases beginning or ending with punctuation.
        return "(?<![[:alnum:]_])(" + escaped + ")(?![[:alnum:]_])";
    }

    public static String highlightPattern(String query) {
        return literalPattern(query.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
    }

    public static String plainText(String expression) {
        // Inline formatting must not split words; block elements supply word separators.
        return PLAIN_TEXT_PREFIX + expression + PLAIN_TEXT_SUFFIX;
    }

    public static String matches(String value, String vector) {
        return "CASE WHEN :fuzzy THEN " + vector + " @@ websearch_to_tsquery('german', :query) "
                + "ELSE " + value + " ~* :literalPattern END";
    }

    public static String matches(String value) {
        return "(" + value + " IS NOT NULL AND " + value + " <> '' AND ("
                + matches(value, "to_tsvector('german', " + value + ")") + "))";
    }

    /** Remove exclusions for a conservative candidate filter across multiple values. */
    public static String candidates(String vector) {
        String indexableQuery = "querytree(websearch_to_tsquery('german', :query))";
        return "CASE WHEN NOT :fuzzy OR " + indexableQuery + " = 'T' THEN TRUE ELSE "
                + vector + " @@ CAST(" + indexableQuery + " AS tsquery) END";
    }

    public static String highlight(String value) {
        return highlight(value, "HighlightAll=true");
    }

    public static String excerpt(String value) {
        String fragment = "CASE WHEN :fuzzy THEN " + value + " ELSE substring(" + value
                + " from GREATEST(1, regexp_instr(" + value + ", :literalPattern, 1, 1, 0, 'i') - 160)"
                + " for char_length(:query) + 320) END";
        return highlight(fragment, "MaxFragments=2, MinWords=8, MaxWords=24, FragmentDelimiter= … ");
    }

    private static String highlight(String value, String options) {
        String escaped = HTML_ESCAPE_PREFIX + value + HTML_ESCAPE_SUFFIX;
        return "CASE WHEN :fuzzy THEN ts_headline('german', " + escaped
                + ", websearch_to_tsquery('german', :query), 'StartSel=<mark>, StopSel=</mark>, " + options + "') "
                + "ELSE regexp_replace(" + escaped + ", :highlightPattern, '<mark>\\1</mark>', 'gi') END";
    }
}
