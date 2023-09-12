create table ancillary.rf_fees
(
	id uuid,
	service_id uuid,
	doctor_id uuid,
	percentage numeric(15,2) default 0,
	use_fixed_value boolean,
	fixed_value numeric,
	created_by varchar(50),
	created_date timestamp,
	last_modified_by varchar(50),
	last_modified_date timestamp,
	deleted boolean
);

