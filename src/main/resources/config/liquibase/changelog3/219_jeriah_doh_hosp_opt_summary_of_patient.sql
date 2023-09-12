CREATE TABLE doh.summary_of_patient (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	total_inpatients int,
	total_newborn int,
	total_discharges int,
	total_pad int,
	total_ibd int,
	total_inpatient_transTo int,
	total_inpatient_transFrom int,
	total_patient_remaining int,
	reporting_year int,


	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);