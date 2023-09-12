ALTER TABLE payroll.timekeeping_employees DROP CONSTRAINT fk_timekeeping_employees_payroll_employee_id;
ALTER TABLE payroll.timekeeping_employees
ADD CONSTRAINT fk_timekeeping_employees_payroll_employee_id FOREIGN KEY(employee) REFERENCES payroll.payroll_employees(id)
ON DELETE CASCADE;

ALTER TABLE payroll.accumulated_logs_summary DROP CONSTRAINT accumulated_logs_summary_timekeeping_employee_fkey;
ALTER TABLE payroll.accumulated_logs_summary
ADD CONSTRAINT accumulated_logs_summary_timekeeping_employee_fkey FOREIGN KEY(timekeeping_employee) REFERENCES payroll.timekeeping_employees(id)
ON DELETE CASCADE;

ALTER TABLE payroll.accumulated_logs DROP CONSTRAINT fk_summary;
ALTER TABLE payroll.accumulated_logs
ADD CONSTRAINT fk_summary FOREIGN KEY(summary) REFERENCES payroll.accumulated_logs_summary(id)
ON DELETE CASCADE;


ALTER TABLE payroll.payroll_employee_allowances DROP CONSTRAINT fk_payroll_employee_allowances_payroll_employee_id;
ALTER TABLE payroll.payroll_employee_allowances
ADD CONSTRAINT fk_payroll_employee_allowances_payroll_employee_id FOREIGN KEY(employee) REFERENCES payroll.payroll_employees(id)
ON DELETE CASCADE;

ALTER TABLE payroll.payroll_employee_allowance_items DROP CONSTRAINT payroll_employee_allowance_fk;
ALTER TABLE payroll.payroll_employee_allowance_items
ADD CONSTRAINT payroll_employee_allowance_fk FOREIGN KEY(payroll_employee_allowance) REFERENCES payroll.payroll_employee_allowances(id)
ON DELETE CASCADE;



ALTER TABLE payroll.other_deduction_employees DROP CONSTRAINT fk_other_deduction_employees_payroll_employee_id;
ALTER TABLE payroll.other_deduction_employees
ADD CONSTRAINT fk_other_deduction_employees_payroll_employee_id FOREIGN KEY(employee) REFERENCES payroll.payroll_employees(id)
ON DELETE CASCADE;

ALTER TABLE payroll.other_deduction_employees_item DROP CONSTRAINT fk_other_deduction_employees_item_other_deduction_employees;
ALTER TABLE payroll.other_deduction_employees_item
ADD CONSTRAINT fk_other_deduction_employees_item_other_deduction_employees FOREIGN KEY(other_deduction_employee) REFERENCES payroll.other_deduction_employees(id)
ON DELETE CASCADE;











