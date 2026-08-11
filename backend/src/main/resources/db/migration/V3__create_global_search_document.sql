CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Excludes internal notes, audit data, coordinates and file metadata.
CREATE MATERIALIZED VIEW global_search_document AS
SELECT
    'BUILDING'::text AS entity_type,
    b.id AS entity_id,
    COALESCE(NULLIF(b.district_property_number, ''), NULLIF(b.property_number, ''), NULLIF(b.object, ''),
             NULLIF((SELECT MIN(bn.name) FROM building_name bn WHERE bn.building_id = b.id), ''),
             NULLIF((SELECT MIN(ban.alt_names) FROM building_alt_names ban WHERE ban.building_id = b.id), ''),
             'Gebäude ' || b.id) AS title,
    NULLIF(CONCAT_WS(' · ', d.name, q.name), '') AS subtitle,
    ARRAY_REMOVE(ARRAY[b.district_property_number, b.property_number, b.object]::text[], NULL)
        || COALESCE((SELECT ARRAY_AGG(DISTINCT bn.name ORDER BY bn.name)
                       FROM building_name bn
                      WHERE bn.building_id = b.id AND NULLIF(bn.name, '') IS NOT NULL), ARRAY[]::text[])
        || COALESCE((SELECT ARRAY_AGG(DISTINCT ban.alt_names ORDER BY ban.alt_names)
                       FROM building_alt_names ban
                      WHERE ban.building_id = b.id AND NULLIF(ban.alt_names, '') IS NOT NULL), ARRAY[]::text[])
        AS title_values,
    CONCAT_WS(' ', b.district_property_number, b.property_number, b.object,
              (SELECT STRING_AGG(DISTINCT bn.name, ' ') FROM building_name bn WHERE bn.building_id = b.id),
              (SELECT STRING_AGG(DISTINCT ban.alt_names, ' ') FROM building_alt_names ban WHERE ban.building_id = b.id)) AS title_text,
    CONCAT_WS(' ', b.parcel_number::text, b.parcel_number_counter::text,
              b.part_type, b.year::text, d.name, q.name, b.general_notes,
              (SELECT STRING_AGG(DISTINCT CONCAT_WS(' ', s.name, a.house_number, a.from_date, a.to_date), ' ')
                 FROM address a LEFT JOIN street s ON s.id = a.street_id WHERE a.building_id = b.id),
              (SELECT STRING_AGG(DISTINCT src.title, ' ')
                 FROM (SELECT bs.source_id FROM building_source bs WHERE bs.building_id = b.id
                       UNION SELECT bl.source_id FROM building_literature bl WHERE bl.building_id = b.id) links
                 JOIN source src ON src.id = links.source_id)) AS metadata_text,
    NULL::text AS full_text,
    NULL::tsvector AS full_text_vector
FROM building b
LEFT JOIN district d ON d.id = b.district_id
LEFT JOIN quarter q ON q.id = b.quarter_id

UNION ALL

SELECT
    'PERSON', p.id,
    COALESCE(NULLIF(p.full_name, ''), NULLIF(CONCAT_WS(' ', p.first_name, p.last_name), ''), 'Person ' || p.id),
    NULLIF(CONCAT_WS(' · ', COALESCE(p.job_original_text, j.name), p.origin_original_text), ''),
    ARRAY_REMOVE(ARRAY[p.full_name, NULLIF(CONCAT_WS(' ', p.first_name, p.last_name), ''), p.first_name, p.last_name]::text[], NULL)
        || COALESCE((SELECT ARRAY_AGG(DISTINCT pan.alt_names ORDER BY pan.alt_names)
                       FROM person_alt_names pan
                      WHERE pan.person_id = p.id AND NULLIF(pan.alt_names, '') IS NOT NULL), ARRAY[]::text[]),
    CONCAT_WS(' ', p.full_name, p.first_name, p.last_name,
              (SELECT STRING_AGG(DISTINCT pan.alt_names, ' ') FROM person_alt_names pan WHERE pan.person_id = p.id)),
    CONCAT_WS(' ', p.sex, p.origin_original_text, p.origin_certainty,
              p.job_original_text, j.name, p.religion_original_text, r.name,
              b.district_property_number, p.general_notes,
              (SELECT STRING_AGG(DISTINCT pl.real_name, ' ')
                 FROM origin_places op JOIN place pl ON pl.id = op.place_id WHERE op.person_id = p.id),
              (SELECT STRING_AGG(DISTINCT CONCAT_WS(' ', w.name, wr.original_text), ' ')
                 FROM weaponry wr LEFT JOIN weapon w ON w.id = wr.weapon_id WHERE wr.person_id = p.id)) AS metadata_text,
    NULL::text, NULL::tsvector
FROM person p
LEFT JOIN job j ON j.id = p.job_category_id
LEFT JOIN religion r ON r.id = p.religion_category_id
LEFT JOIN building b ON b.id = p.building_id

UNION ALL

SELECT
    'PLACE', p.id, COALESCE(NULLIF(p.real_name, ''), 'Ort ' || p.id),
    CASE WHEN p.is_uncertain THEN 'Unsichere Zuordnung' ELSE NULL END,
    ARRAY_REMOVE(ARRAY[p.real_name]::text[], NULL)
        || COALESCE((SELECT ARRAY_AGG(DISTINCT pan.alt_names ORDER BY pan.alt_names)
                       FROM place_alt_names pan
                      WHERE pan.place_id = p.id AND NULLIF(pan.alt_names, '') IS NOT NULL), ARRAY[]::text[]),
    CONCAT_WS(' ', p.real_name,
              (SELECT STRING_AGG(DISTINCT pan.alt_names, ' ') FROM place_alt_names pan WHERE pan.place_id = p.id)),
    COALESCE(p.general_notes, ''), NULL::text, NULL::tsvector
FROM place p

UNION ALL

SELECT
    'CITIZENSHIP', c.id, COALESCE(NULLIF(c.signature, ''), 'Eintrag ' || c.id),
    naturalized.full_name,
    ARRAY_REMOVE(ARRAY[c.signature]::text[], NULL),
    COALESCE(c.signature, ''),
    CONCAT_WS(' ', c.ref_number, c.date_naturalization, c.date_misc, naturalized.full_name,
              primary_source.title, primary_source.signature, secondary_source.title, secondary_source.signature,
              c.addendum, c.general_notes,
              (SELECT STRING_AGG(DISTINCT mentioned.full_name, ' ')
                 FROM citizenship_mentioned_person cmp
                 JOIN person mentioned ON mentioned.id = cmp.person_id
                WHERE cmp.citizenship_id = c.id)) AS metadata_text,
    c.entry_text, c.full_text_vector
FROM citizenship c
LEFT JOIN person naturalized ON naturalized.id = c.person_id
LEFT JOIN source primary_source ON primary_source.id = c.primary_source_id
LEFT JOIN source secondary_source ON secondary_source.id = c.secondary_source_id

UNION ALL

SELECT
    'SOURCE', s.id, COALESCE(NULLIF(s.title, ''), 'Quelle ' || s.id),
    NULLIF(CONCAT_WS(' · ', s.type, s.signature), ''),
    ARRAY_REMOVE(ARRAY[s.title]::text[], NULL),
    COALESCE(s.title, ''),
    CONCAT_WS(' ', s.type, s.signature, s.description, s.general_notes,
              (SELECT STRING_AGG(DISTINCT sa.authors, ' ') FROM source_authors sa WHERE sa.source_id = s.id)),
    NULL::text, NULL::tsvector
FROM source s

UNION ALL

SELECT
    'STREET', s.id, COALESCE(NULLIF(s.name, ''), 'Straße ' || s.id), NULL::text,
    ARRAY_REMOVE(ARRAY[s.name]::text[], NULL)
        || COALESCE((SELECT ARRAY_AGG(DISTINCT san.alt_names ORDER BY san.alt_names)
                       FROM street_alt_names san
                      WHERE san.street_id = s.id AND NULLIF(san.alt_names, '') IS NOT NULL), ARRAY[]::text[]),
    CONCAT_WS(' ', s.name,
              (SELECT STRING_AGG(DISTINCT san.alt_names, ' ') FROM street_alt_names san WHERE san.street_id = s.id)),
    CONCAT_WS(' ', s.description, s.general_notes), NULL::text, NULL::tsvector
FROM street s

UNION ALL

SELECT
    'DISTRICT', d.id, COALESCE(NULLIF(d.name, ''), 'Distrikt ' || d.id), NULL::text,
    ARRAY_REMOVE(ARRAY[d.name]::text[], NULL),
    COALESCE(d.name, ''), CONCAT_WS(' ', d.description, d.general_notes), NULL::text, NULL::tsvector
FROM district d

UNION ALL

SELECT
    'QUARTER', q.id, COALESCE(NULLIF(q.name, ''), 'Viertel ' || q.id), NULL::text,
    ARRAY_REMOVE(ARRAY[q.name]::text[], NULL),
    COALESCE(q.name, ''), CONCAT_WS(' ', q.description, q.general_notes), NULL::text, NULL::tsvector
FROM quarter q;

CREATE UNIQUE INDEX idx_global_search_document_identity
    ON global_search_document (entity_type, entity_id);

CREATE INDEX idx_global_search_document_entity_type
    ON global_search_document (entity_type);

CREATE INDEX idx_global_search_document_title_trgm
    ON global_search_document
    USING GIN (LOWER(title_text) gin_trgm_ops);

CREATE INDEX idx_global_search_document_metadata_trgm
    ON global_search_document
    USING GIN (LOWER(metadata_text) gin_trgm_ops);

CREATE INDEX idx_global_search_document_full_text_vector
    ON global_search_document
    USING GIN (full_text_vector);
