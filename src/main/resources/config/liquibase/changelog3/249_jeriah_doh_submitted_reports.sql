CREATE TABLE doh.submitted_reports (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	reporting_year int,
	reporting_status varchar,
	reported_by varchar,
	designation varchar,
	sections varchar,
	department varchar,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);