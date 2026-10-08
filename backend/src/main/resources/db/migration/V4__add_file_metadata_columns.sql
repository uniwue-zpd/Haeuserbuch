ALTER TABLE public.file
    ADD COLUMN document_category character varying(255),
    ADD COLUMN document_type character varying(255),
    ADD COLUMN date_captured character varying(255),
    ADD COLUMN date_captured_precision character varying(255),
    ADD COLUMN date_from character varying(255),
    ADD COLUMN date_to character varying(255),
    ADD COLUMN source character varying(255),
    ADD COLUMN collection character varying(255),
    ADD COLUMN signature character varying(255),
    ADD COLUMN creator character varying(255),
    ADD COLUMN rights_holder character varying(255),
    ADD COLUMN license character varying(255),
    ADD COLUMN download_allowed boolean,
    ADD COLUMN description text,
    ADD COLUMN caption character varying(255),
    ADD COLUMN source_url character varying(255);

ALTER TABLE public.file
    ADD CONSTRAINT file_document_type_check CHECK (document_type IN
        ('PHOTOGRAPH', 'PLAN', 'MAP', 'SITE_PLAN', 'FLOOR_PLAN', 'ELEVATION', 'SECTION', 'OTHER'));
