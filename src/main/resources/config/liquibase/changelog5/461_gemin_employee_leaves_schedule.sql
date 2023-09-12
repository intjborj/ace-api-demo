
ALTER TABLE IF EXISTS hrm.employee_leave_request RENAME TO employee_request;

ALTER TABLE hrm.employee_request ALTER COLUMN leave_type TYPE varchar(50);
