DROP TABLE IF EXISTS appointment.schedule_time;
CREATE TABLE appointment.schedule_time (
    id                  uuid NOT NULL primary key,
    schedule            uuid null,
	config              uuid null,
	max_person          int,
	allowed_stat        bool default false,
	stat_slot           int,
	status              bool default false,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (schedule) references appointment.schedule(id),
	foreign key (config) references appointment.config(id)
);