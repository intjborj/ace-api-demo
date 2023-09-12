CREATE TABLE doh.discharge_er (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	er_consultations varchar,
	number int,
	icd_10_code varchar,
	icd_10_category varchar,
	reporting_year int,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);