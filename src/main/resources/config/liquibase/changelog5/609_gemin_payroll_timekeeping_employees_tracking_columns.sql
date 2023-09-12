

--Payroll Employee Timekeeping
ALTER TABLE payroll.timekeeping_employees
ADD COLUMN finalized_by  uuid
                         constraint timekeeping_employee_finalized_by_employees_id
                         references hrm.employees(id)
                         on update cascade on delete restrict;

ALTER TABLE payroll.timekeeping_employees
ADD COLUMN finalized_date  timestamp;

ALTER TABLE payroll.timekeeping_employees
ADD COLUMN rejected_by   uuid
                         constraint timekeeping_employee_employee_rejected_by_employees_id
                         references hrm.employees(id)
                         on update cascade on delete restrict;

ALTER TABLE payroll.timekeeping_employees
ADD COLUMN rejected_date  timestamp;

ALTER TABLE payroll.timekeeping_employees
ADD COLUMN approved_by   uuid
                         constraint timekeeping_employee_approved_by_employees_id
                         references hrm.employees(id)
                         on update cascade on delete restrict;

ALTER TABLE payroll.timekeeping_employees
ADD COLUMN approved_date  timestamp;
