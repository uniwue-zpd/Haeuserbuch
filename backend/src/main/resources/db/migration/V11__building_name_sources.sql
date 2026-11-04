-- Building names are re-imported with the new structure (several sources per name, each with an excerpt).
-- Existing names are not migrated: they are removed together with their old single-source column.
DELETE FROM public.building_name;

ALTER TABLE public.building_name DROP COLUMN source_id;

CREATE TABLE public.building_name_source (
    building_name_id bigint NOT NULL,
    position integer NOT NULL,
    source_id bigint,
    excerpt text,
    CONSTRAINT building_name_source_pkey PRIMARY KEY (building_name_id, position),
    CONSTRAINT fk_building_name_source_name FOREIGN KEY (building_name_id) REFERENCES public.building_name(id) ON DELETE CASCADE,
    CONSTRAINT fk_building_name_source_source FOREIGN KEY (source_id) REFERENCES public.source(id)
);

CREATE INDEX idx_building_name_source_source_id ON public.building_name_source (source_id);

-- The global search document contains building names; drop the removed ones from it.
REFRESH MATERIALIZED VIEW public.global_search_document;
