ALTER TABLE pms.cases
    ADD COLUMN rvs_list VARCHAR,
    ADD COLUMN icd_list VARCHAR,
    ADD COLUMN primary_dx VARCHAR,
    ADD COLUMN secondary_dx VARCHAR;