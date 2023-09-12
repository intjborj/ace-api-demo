
ALTER TABLE hrm.employee_schedule
ADD COLUMN label            varchar(255);

ALTER TABLE hrm.employee_schedule
ADD COLUMN title            varchar(255);

ALTER TABLE hrm.employee_schedule
ADD COLUMN locked           boolean;
