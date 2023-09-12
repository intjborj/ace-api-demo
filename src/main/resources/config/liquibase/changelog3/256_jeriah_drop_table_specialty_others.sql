DROP TABLE  if EXISTS doh.discharge_specialty_other;
CREATE TABLE doh.discharge_specialty_others (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
    	other_type_services_specify varchar,
    	no_patients int,
    	total_length_stay int,
    	non_philhealth_pay int,
    	nph_service_charity int,
    	total_non_philhealth int,
    	philhealth_pay int,
    	philhealth_services int,
    	total_philhealth int,
    	hmo int,
    	owwa int,
    	recovered_improved int,
    	transferred int,
    	hama int,
    	absconded int,
    	unimproved int,
    	death_below_48_hours int,
    	death_over_48 int,
    	total_deaths int,
    	total_discharge int,
    	remarks varchar,
    	reporting_year timestamp    default CURRENT_TIMESTAMP,


    	created_by         varchar(50),
        created_date       timestamp(6) default CURRENT_TIMESTAMP,
        last_modified_by   varchar(50),
        last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);
