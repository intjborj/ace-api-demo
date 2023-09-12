CREATE SCHEMA IF NOT EXISTS appointment;

DROP TABLE IF EXISTS appointment.config;
CREATE TABLE appointment.config (
    id                  uuid NOT NULL primary key,
	code                varchar null,
	t_start             time not null,
	t_end               time not null,
	status              bool default false,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL
);