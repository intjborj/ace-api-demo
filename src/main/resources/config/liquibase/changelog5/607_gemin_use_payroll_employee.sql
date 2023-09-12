ALTER TABLE payroll.timekeeping_employees DROP CONSTRAINT fk_timekeeping_employees_employee_id;
ALTER TABLE payroll.payroll_employee_allowances DROP CONSTRAINT employee_fk;
ALTER TABLE payroll.other_deduction_employees DROP CONSTRAINT fk_other_deduction_employee_employee;

ALTER TABLE payroll.timekeeping_employees
    ADD CONSTRAINT fk_timekeeping_employees_payroll_employee_id
    FOREIGN KEY (employee) REFERENCES payroll.payroll_employees (id)
    ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE payroll.payroll_employee_allowances
    ADD CONSTRAINT fk_payroll_employee_allowances_payroll_employee_id
    FOREIGN KEY (employee) REFERENCES payroll.payroll_employees (id)
    ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE payroll.other_deduction_employees rename column employees to employee;

ALTER TABLE payroll.other_deduction_employees
    ADD CONSTRAINT fk_other_deduction_employees_payroll_employee_id
    FOREIGN KEY (employee) REFERENCES payroll.payroll_employees (id)
    ON DELETE RESTRICT ON UPDATE CASCADE;
