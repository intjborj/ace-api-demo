DROP TABLE IF EXISTS appointment.appointment;
CREATE TABLE appointment.appointment (
	id                  uuid primary key NOT NULL,
	app_no              varchar(50) NULL,
	patient             uuid NULL,
	schedule            uuid NULL,
	schedule_time       uuid NULL,
	order_status        varchar(50) NULL,
	status              varchar(50) NULL, -- arrive and serve, not arrived

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT CURRENT_TIMESTAMP,

    foreign key (patient) references appointment.patients(id),
    foreign key (schedule) references appointment.schedule(id),
	foreign key (schedule_time) references appointment.schedule_time(id)
);