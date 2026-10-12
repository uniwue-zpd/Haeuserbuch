ALTER TABLE public.ownership_record
    ADD COLUMN sum BIGINT;

CREATE TABLE public.ownership_record_building
(
    ownership_record_id BIGINT NOT NULL,
    building_id         BIGINT NOT NULL,
    CONSTRAINT pk_ownership_record_building PRIMARY KEY (ownership_record_id, building_id),
    CONSTRAINT fk_ownership_record_building_record
        FOREIGN KEY (ownership_record_id) REFERENCES public.ownership_record (id) ON DELETE CASCADE,
    CONSTRAINT fk_ownership_record_building_building
        FOREIGN KEY (building_id) REFERENCES public.building (id) ON DELETE CASCADE
);

CREATE INDEX idx_ownership_record_building_building
    ON public.ownership_record_building (building_id);
