CREATE TABLE accounting.release_checks (
	id                  uuid NOT NULL primary key,
	release_date        date NULL,
	checks              uuid null,
	is_posted           bool,
	release_by          varchar,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (checks) references accounting.disbursement_check(id)
);