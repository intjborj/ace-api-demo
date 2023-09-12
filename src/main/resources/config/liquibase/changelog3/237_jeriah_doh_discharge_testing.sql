CREATE TABLE doh.discharge_testing (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	testing_group varchar,
	testing varchar,
	number int,
	reporting_year int,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);