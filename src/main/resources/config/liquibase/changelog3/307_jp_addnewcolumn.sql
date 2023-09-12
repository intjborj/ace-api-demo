ALTER TABLE doh.bed_capacity
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.classifications
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.discharge_er
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.discharge_ev
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.discharge_opd
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.discharge_opv
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.discharge_specialty
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.discharge_specialty_others
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.discharge_testing
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.discharges_morbidity
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.expenses
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.hosp_quality_management
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.mortality_death
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.number_deliveries
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.operation_deaths
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.operation_hai
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.operation_major_opt
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.operation_minor_opt
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.quality_management
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.revenues
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.staffing_pattern
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.staffing_pattern_others
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.submitted_reports
    ADD COLUMN doh_response character varying;

ALTER TABLE doh.summary_of_patient
    ADD COLUMN doh_response character varying;