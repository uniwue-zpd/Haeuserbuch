ALTER TABLE ownerhsip_record RENAME TO ownership_record;

ALTER TABLE ownership_record ADD COLUMN signature VARCHAR(255);
