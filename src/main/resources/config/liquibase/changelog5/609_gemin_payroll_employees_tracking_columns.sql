--Payroll Employee Allowance
ALTER TABLE payroll.payroll_employee_allowances
ADD COLUMN finalized_by  uuid
                         constraint payroll_employee_allowances_finalized_by_employees_id
                         references hrm.employees(id)
                         on update cascade on delete restrict;

ALTER TABLE payroll.payroll_employee_allowances
ADD COLUMN finalized_date  timestamp;

ALTER TABLE payroll.payroll_employee_allowances
ADD COLUMN rejected_by   uuid
                         constraint payroll_employee_allowances_rejected_by_employees_id
                         references hrm.employees(id)
                         on update cascade on delete restrict;

ALTER TABLE payroll.payroll_employee_allowances
ADD COLUMN rejected_date  timestamp;

--Payroll Employee Contribution
ALTER TABLE payroll.payroll_employee_contributions
ADD COLUMN finalized_by  uuid
                         constraint payroll_employee_contributions_finalized_by_employees_id
                         references hrm.employees(id)
                         on update cascade on delete restrict;

ALTER TABLE payroll.payroll_employee_contributions
ADD COLUMN finalized_date  timestamp;

ALTER TABLE payroll.payroll_employee_contributions
ADD COLUMN rejected_by   uuid
                         constraint payroll_employee_contributions_rejected_by_employees_id
                         references hrm.employees(id)
                         on update cascade on delete restrict;

ALTER TABLE payroll.payroll_employee_contributions
ADD COLUMN rejected_date  timestamp;


--Payroll Employee Other Deduction
ALTER TABLE payroll.other_deduction_employees
ADD COLUMN finalized_by  uuid
                         constraint other_deduction_employee_finalized_by_employees_id
                         references hrm.employees(id)
                         on update cascade on delete restrict;

ALTER TABLE payroll.other_deduction_employees
ADD COLUMN finalized_date  timestamp;

ALTER TABLE payroll.other_deduction_employees
ADD COLUMN rejected_by   uuid
                         constraint other_deduction_employee_rejected_by_employees_id
                         references hrm.employees(id)
                         on update cascade on delete restrict;

ALTER TABLE payroll.other_deduction_employees
ADD COLUMN rejected_date  timestamp;

ALTER TABLE payroll.other_deduction_employees
ADD COLUMN approved_by   uuid
                         constraint other_deduction_employee_approved_by_employees_id
                         references hrm.employees(id)
                         on update cascade on delete restrict;

ALTER TABLE payroll.other_deduction_employees
ADD COLUMN approved_date  timestamp;
