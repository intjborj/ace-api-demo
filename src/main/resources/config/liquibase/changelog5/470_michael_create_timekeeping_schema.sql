CREATE SCHEMA payroll;

ALTER TABLE hrm.accumulated_logs
SET SCHEMA payroll;

ALTER TABLE hrm.timekeeping_employees
SET SCHEMA payroll;

ALTER TABLE hrm.timekeepings
SET SCHEMA payroll;

