create table ancillary.notes
(
	id uuid,
	domain_name varchar(100),
	pk_id uuid,
	details varchar(200),
	status varchar(50),
	created_by varchar(50),
	created_date timestamp default CURRENT_TIMESTAMP,
	entry_datetime timestamp without time zone default CURRENT_TIMESTAMP,
	last_modified_by varchar(50),
	last_modified_date timestamp default CURRENT_TIMESTAMP,
	deleted boolean
);