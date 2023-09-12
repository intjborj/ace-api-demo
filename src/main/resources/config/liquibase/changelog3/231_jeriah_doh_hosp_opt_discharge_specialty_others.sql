CREATE TABLE doh.discharge_specialty_others(
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	other_type_services_specify varchar,
	no_patients int,
	total_length_stay int,
	non_philhealt_pay varchar,
	nph_service_charity varchar,
	total_non_philhealth varchar,
	philhealth_pay varchar,
	total_philhealth int,
	hmo varchar,
	owwa varchar,
	recovered_improved varchar,
	transferred varchar,
	hama varchar,
	absconded varchar,
	unimproved varchar,
	death_below_48_hours varchar,
	death_over_48 varchar,
	total_deaths int,
	total_discharge int,
	remarks varchar,
	reporting_year int,


	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);