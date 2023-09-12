ALTER TABLE doh.discharges_morbidity DROP COLUMN diagnosis_category;

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN diagnosis_category character varying;

ALTER TABLE doh.discharges_morbidity DROP COLUMN icd_10_code;

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN icd_10_code character varying;

ALTER TABLE doh.discharges_morbidity
    RENAME male_30_39 TO male_30_34;

ALTER TABLE doh.discharges_morbidity
    RENAME female_30_39 TO female_30_34;

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN female_35_39 integer;

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN male_35_39 integer;