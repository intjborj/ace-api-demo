CREATE TABLE hrm.shifts (
	id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
	from_time time,
	to_time time
);