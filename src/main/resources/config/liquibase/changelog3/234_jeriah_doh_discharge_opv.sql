CREATE TABLE doh.discharge_opv (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),

	new_patient int,
	revisit int,
	adult int,
	pediatric int,
	adult_general_medicine int,
	specialty_non_surgical int,
	surgical int,
	antenatal int,
	postnatal int,
	reporting_year int,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);