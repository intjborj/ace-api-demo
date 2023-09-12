ALTER TABLE doh.discharges_morbidity DROP COLUMN male_30_39;

ALTER TABLE doh.discharges_morbidity DROP COLUMN female_30_39;

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN male_30_34 int;

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN female_30_34 int;

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN   male_35_39 int;

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN female_35_39 int;