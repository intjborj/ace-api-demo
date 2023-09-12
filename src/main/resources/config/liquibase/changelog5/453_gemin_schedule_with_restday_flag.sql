ALTER TABLE hrm.employee_schedule
ADD COLUMN with_rest bool default false;

ALTER TABLE hrm.employee_schedule
ADD COLUMN with_nsd bool default true;