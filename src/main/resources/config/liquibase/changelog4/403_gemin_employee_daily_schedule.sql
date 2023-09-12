
ALTER TABLE hrm.employee_schedule
ADD FOREIGN KEY (employee)
REFERENCES hrm.employees(id)
