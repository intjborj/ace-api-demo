CREATE TABLE doh.discharge_specialty (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	type_of_service varchar,
	no_patient varchar,
	total_length_stay varchar,
	non_philhealth_stay varchar,
	nhp_service_charity varchar,
	total_non_philhealth varchar,
	philhealth_pay varchar,
	philhealth_service varchar,
	total_philhealth varchar,
	hmo varchar,
	owwa varchar,
	recovered_improved varchar,
	transferred varchar,
	hama varchar,
	absconded varchar,
	unimproved varchar,
	deaths_below_48_hours int,
	deaths_over_48 int,
	total_deaths int,
	total_discharge int,
	remarks  varchar,
	reporting_year int,


	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);