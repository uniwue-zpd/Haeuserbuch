-- Derived year of naturalization: first 4-digit group of the free-text date (e.g. '08.04.1412' -> 1412)
ALTER TABLE citizenship
    ADD COLUMN year_naturalization integer
        GENERATED ALWAYS AS (CAST(substring(date_naturalization FROM '[0-9]{4}') AS integer)) STORED;

CREATE INDEX idx_citizenship_year_naturalization ON citizenship (year_naturalization);
