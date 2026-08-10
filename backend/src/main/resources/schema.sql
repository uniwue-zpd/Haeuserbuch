-- Citizenhip register. Add tsvector column if it does not exist
ALTER TABLE citizenship
    ADD COLUMN IF NOT EXISTS full_text_vector tsvector
    GENERATED ALWAYS AS (to_tsvector('german', entry_text)) STORED;

-- Citizenship register. Create GIN index
CREATE INDEX IF NOT EXISTS idx_full_text_vector_gin
    ON citizenship
    USING GIN (full_text_vector);
