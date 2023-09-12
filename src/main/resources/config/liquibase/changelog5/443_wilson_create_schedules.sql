DROP TABLE IF EXISTS appointment.schedule;
CREATE TABLE appointment.schedule (
    id                  uuid NOT NULL primary key,
    schedule_code       varchar null,
	schedule_date       date,
	status              bool default false,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL
);