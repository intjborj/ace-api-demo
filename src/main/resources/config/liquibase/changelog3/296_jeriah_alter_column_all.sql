ALTER TABLE doh.discharge_er
DROP COLUMN reporting_year;
ALTER TABLE doh.discharge_er
ADD COLUMN reporting_year int;

ALTER TABLE doh.discharge_ev
DROP COLUMN reporting_year;
ALTER TABLE doh.discharge_ev
ADD COLUMN reporting_year int;

ALTER TABLE doh.discharge_opv
DROP COLUMN reporting_year;
ALTER TABLE doh.discharge_opv
ADD COLUMN reporting_year int;

ALTER TABLE doh.discharge_specialty
DROP COLUMN reporting_year;
ALTER TABLE doh.discharge_specialty
ADD COLUMN reporting_year int;

ALTER TABLE doh.discharge_specialty_others
DROP COLUMN reporting_year;
ALTER TABLE doh.discharge_specialty_others
ADD COLUMN reporting_year int;

ALTER TABLE doh.discharge_testing
DROP COLUMN reporting_year;
ALTER TABLE doh.discharge_testing
ADD COLUMN reporting_year int;

ALTER TABLE doh.discharges_morbidity
DROP COLUMN reporting_year;
ALTER TABLE doh.discharges_morbidity
ADD COLUMN reporting_year int;

ALTER TABLE doh.hosp_quality_management
DROP COLUMN reporting_year;
ALTER TABLE doh.hosp_quality_management
ADD COLUMN reporting_year int;

ALTER TABLE doh.mortality_death
DROP COLUMN reportingyear;
ALTER TABLE doh.mortality_death
ADD COLUMN reporting_year int;

ALTER TABLE doh.number_deliveries
DROP COLUMN reporting_year;
ALTER TABLE doh.number_deliveries
ADD COLUMN reporting_year int;

ALTER TABLE doh.operation_deaths
DROP COLUMN reporting_year;
ALTER TABLE doh.operation_deaths
ADD COLUMN reporting_year int;

ALTER TABLE doh.operation_hai
DROP COLUMN reportingyear;
ALTER TABLE doh.operation_hai
ADD COLUMN reporting_year int;

ALTER TABLE doh.operation_minor_opt
DROP COLUMN reporting_year;
ALTER TABLE doh.operation_minor_opt
ADD COLUMN reporting_year int;

ALTER TABLE doh.operation_minor_opt
DROP COLUMN reporting_year;
ALTER TABLE doh.operation_minor_opt
ADD COLUMN reporting_year int;

ALTER TABLE doh.staffing_pattern
DROP COLUMN reporting_year;
ALTER TABLE doh.staffing_pattern
ADD COLUMN reporting_year int;

ALTER TABLE doh.staffing_pattern_others
DROP COLUMN reportingyear;
ALTER TABLE doh.staffing_pattern_others
ADD COLUMN reporting_year int;

ALTER TABLE doh.submitted_reports
DROP COLUMN reporting_year;
ALTER TABLE doh.submitted_reports
ADD COLUMN reporting_year int;

ALTER TABLE doh.summary_of_patient
DROP COLUMN reporting_year;
ALTER TABLE doh.summary_of_patient
ADD COLUMN reporting_year int;







