CREATE TABLE doh.operation_deaths (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	total_death int,
	total_death_48_down int,
	total_death_48_up int,
	total_er_deaths int,
	total_doa int,
	total_still_birth int,
	total_neonatal_death int,
	total_maternal_death int,
	total_discharge_death int,
	gross_death_rate int,
	ndr_numerator int,
	ndr_denominator int,
	net_death_rate decimal,
	reporting_year int,


	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);