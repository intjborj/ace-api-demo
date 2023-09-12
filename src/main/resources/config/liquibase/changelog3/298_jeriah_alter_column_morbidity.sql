ALTER TABLE doh.discharges_morbidity DROP COLUMN icd_10_code;

ALTER TABLE doh.discharges_morbidity DROP COLUMN diagnosis_category;

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN icd_10_code "char";

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN diagnosis_category "char";