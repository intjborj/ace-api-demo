CREATE TABLE doh.bed_capacity (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	abc  varchar,
	implementing_beds varchar,
	bor varchar,
	reporting_year int,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);