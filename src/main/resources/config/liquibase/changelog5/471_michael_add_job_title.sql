DROP table if exists hrm.job_title;
CREATE TABLE hrm.job_title (
    id                  uuid NOT NULL PRIMARY KEY,
	value               varchar NOT NULL UNIQUE,
	label               varchar null ,
    status              varchar null,

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL
);