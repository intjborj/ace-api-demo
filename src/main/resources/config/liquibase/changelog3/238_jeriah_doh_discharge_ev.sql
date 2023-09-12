CREATE TABLE doh.discharge_ev(
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	emergency_visits int,
	emergency_visits_adult int,
	emergency_visits_pediatric int,
	ev_from_facility_to_another int,
	reporting_year int,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);