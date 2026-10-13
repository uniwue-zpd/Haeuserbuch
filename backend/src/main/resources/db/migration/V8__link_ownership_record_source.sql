ALTER TABLE public.ownership_record
    ADD COLUMN source_id BIGINT,
    ADD CONSTRAINT fk_ownership_record_source
        FOREIGN KEY (source_id) REFERENCES public.source (id) ON DELETE SET NULL;

CREATE INDEX idx_ownership_record_source
    ON public.ownership_record (source_id);
