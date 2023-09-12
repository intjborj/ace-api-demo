ALTER TABLE hrm.employee_allowance
ADD name  varchar NOT NULL,
ADD amount NUMERIC(15,2) NOT NULL,
ADD taxable BOOLEAN DEFAULT(FALSE) NOT NULL;

ALTER TABLE hrm.employee_allowance
DROP COLUMN allowance_id;

ALTER TABLE hrm.employee_allowance
RENAME COLUMN employee_id TO employee;