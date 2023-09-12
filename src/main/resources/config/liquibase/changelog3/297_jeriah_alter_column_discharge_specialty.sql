ALTER TABLE doh.discharge_specialty DROP COLUMN type_of_service;

ALTER TABLE doh.discharge_specialty DROP COLUMN no_patient;

ALTER TABLE doh.discharge_specialty DROP COLUMN total_length_stay;

ALTER TABLE doh.discharge_specialty DROP COLUMN non_philhealth_stay;

ALTER TABLE doh.discharge_specialty DROP COLUMN nhp_service_charity;

ALTER TABLE doh.discharge_specialty DROP COLUMN total_non_philhealth;

ALTER TABLE doh.discharge_specialty DROP COLUMN philhealth_pay;

ALTER TABLE doh.discharge_specialty DROP COLUMN philhealth_service;

ALTER TABLE doh.discharge_specialty DROP COLUMN total_philhealth;

ALTER TABLE doh.discharge_specialty DROP COLUMN hmo;

ALTER TABLE doh.discharge_specialty DROP COLUMN owwa;

ALTER TABLE doh.discharge_specialty DROP COLUMN recovered_improved;

ALTER TABLE doh.discharge_specialty DROP COLUMN transferred;

ALTER TABLE doh.discharge_specialty DROP COLUMN hama;

ALTER TABLE doh.discharge_specialty DROP COLUMN absconded;

ALTER TABLE doh.discharge_specialty DROP COLUMN unimproved;

ALTER TABLE doh.discharge_specialty DROP COLUMN deaths_below_48_hours;

ALTER TABLE doh.discharge_specialty DROP COLUMN deaths_over_48;

ALTER TABLE doh.discharge_specialty DROP COLUMN total_deaths;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN type_of_service integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN no_patient integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN total_length_stay integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN non_philhealth_stay integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN nhp_service_charity integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN total_non_philhealth integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN philhealth_pay integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN philhealth_service integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN total_philhealth integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN hmo integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN owwa integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN recovered_improved integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN transferred integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN hama integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN absconded integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN unimproved integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN deaths_below_48_hours integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN deaths_over_48 integer;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN total_deaths integer;