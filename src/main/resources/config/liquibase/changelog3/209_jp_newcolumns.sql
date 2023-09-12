ALTER TABLE pms.patient_philhealth_data
    ADD COLUMN member_gender character varying;

ALTER TABLE pms.patient_philhealth_data
    ADD COLUMN member_address character varying;

ALTER TABLE pms.patient_philhealth_data
    ADD COLUMN member_country character varying;

ALTER TABLE pms.patient_philhealth_data
    ADD COLUMN member_state_province character varying;

ALTER TABLE pms.patient_philhealth_data
    ADD COLUMN member_city_municipality character varying;

ALTER TABLE pms.patient_philhealth_data
    ADD COLUMN member_barangay character varying;

ALTER TABLE pms.patient_philhealth_data
    ADD COLUMN member_zip_code character varying;

ALTER TABLE pms.cases
    ADD COLUMN company_pen character varying;