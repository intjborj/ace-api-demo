ALTER TABLE doh.classifications
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.create_rvs_account
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.discharge_er
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.discharge_ev
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.discharge_opd
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.discharge_opv
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.discharge_specialty
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.discharge_specialty_others
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.discharge_testing
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.discharges_morbidity
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.expenses
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.hosp_quality_management
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.mortality_death
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.number_deliveries
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.operation_deaths
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.operation_hai
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.operation_major_opt
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.operation_minor_opt
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.revenues
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.staffing_pattern
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.staffing_pattern_others
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.submitted_reports
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;

ALTER TABLE doh.summary_of_patient
ADD submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;



