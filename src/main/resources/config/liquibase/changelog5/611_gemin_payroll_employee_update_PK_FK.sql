-- Payroll Employee Other Deduction
ALTER TABLE payroll.other_deduction_employees_item DROP CONSTRAINT fk_other_deduction_employees_item_other_deduction_employees;
ALTER TABLE payroll.other_deduction_employees DROP CONSTRAINT fk_other_deduction_employee_other_deduction;
ALTER TABLE payroll.other_deduction_employees DROP COLUMN id;

ALTER TABLE payroll.other_deduction_employees ADD PRIMARY KEY (employee);

ALTER TABLE payroll.other_deduction_employees_item
    ADD CONSTRAINT fk_other_deduction_employees_item_other_deduction_employees_employee
    FOREIGN KEY (other_deduction_employee)
    REFERENCES payroll.other_deduction_employees(employee)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Payroll Employee Allowances
ALTER TABLE payroll.payroll_employee_allowance_items DROP CONSTRAINT payroll_employee_allowance_fk;
ALTER TABLE payroll.payroll_employee_allowances DROP CONSTRAINT payroll_allowance_fk;
ALTER TABLE payroll.payroll_employee_allowances DROP COLUMN id;

ALTER TABLE payroll.payroll_employee_allowances ADD PRIMARY KEY (employee);

ALTER TABLE payroll.payroll_employee_allowance_items
    ADD CONSTRAINT fk_employee_allowance_employee_allowance_items_employee
    FOREIGN KEY (payroll_employee_allowance)
    REFERENCES payroll.payroll_employee_allowances(employee)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Payroll Employee Contribution
ALTER TABLE payroll.payroll_employee_contributions DROP CONSTRAINT payroll_contribution_fk;
ALTER TABLE payroll.payroll_employee_contributions DROP COLUMN id;

ALTER TABLE payroll.payroll_employee_contributions
    ADD CONSTRAINT fk_payroll_employee_contributions_payroll_employee_id
    FOREIGN KEY (employee)
    REFERENCES payroll.payroll_employees(id)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Payroll Employee Timekeeping
ALTER TABLE payroll.accumulated_logs_summary DROP CONSTRAINT accumulated_logs_summary_timekeeping_employee_fkey;
ALTER TABLE payroll.timekeeping_employees DROP CONSTRAINT fk_timekeeping_employees_timekeeping_id;
ALTER TABLE payroll.timekeeping_employees DROP COLUMN id;

ALTER TABLE payroll.timekeeping_employees ADD PRIMARY KEY (employee);

ALTER TABLE payroll.accumulated_logs_summary
    ADD CONSTRAINT fk_summary_timekeeping_employee_employee
    FOREIGN KEY (timekeeping_employee)
    REFERENCES payroll.timekeeping_employees(employee)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Payroll Timekeeping
ALTER TABLE payroll.timekeepings DROP CONSTRAINT fk_timekeepings_payroll_id;
ALTER TABLE payroll.timekeepings DROP COLUMN id;

ALTER TABLE payroll.timekeepings ADD PRIMARY KEY (payroll);

ALTER TABLE payroll.timekeepings
    ADD CONSTRAINT fk_timekeepings_payroll__id
    FOREIGN KEY (payroll)
    REFERENCES payroll.payrolls(id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE payroll.timekeeping_employees
    ADD CONSTRAINT fk_timekeeping_employees_timekeepings_payroll
    FOREIGN KEY (timekeeping)
    REFERENCES payroll.timekeepings(payroll)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Payroll Contribution
ALTER TABLE payroll.payroll_contributions DROP CONSTRAINT payroll_fk;
ALTER TABLE payroll.payroll_contributions DROP COLUMN id;

ALTER TABLE payroll.payroll_contributions ADD PRIMARY KEY (payroll);

ALTER TABLE payroll.payroll_contributions
    ADD CONSTRAINT fk_payroll_contributions_payroll__id
    FOREIGN KEY (payroll)
    REFERENCES payroll.payrolls(id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE payroll.payroll_employee_contributions
    ADD CONSTRAINT fk_payroll_employee_contributions_payroll_contributions_payroll
    FOREIGN KEY (payroll_contribution)
    REFERENCES payroll.payroll_contributions(payroll)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Payroll Allowances
ALTER TABLE payroll.payroll_allowances DROP CONSTRAINT payroll_fk;
ALTER TABLE payroll.payroll_allowances DROP COLUMN id;
ALTER TABLE payroll.payroll_allowances ADD PRIMARY KEY (payroll);

ALTER TABLE payroll.payroll_allowances
    ADD CONSTRAINT fk_payroll_allowances_payroll__id
    FOREIGN KEY (payroll)
    REFERENCES payroll.payrolls(id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE payroll.payroll_employee_allowances
    ADD CONSTRAINT fk_payroll_employee_allowances_payroll_allowances_payroll
    FOREIGN KEY (payroll_allowance)
    REFERENCES payroll.payroll_allowances(payroll)
    ON DELETE CASCADE ON UPDATE CASCADE;

-- Payroll Other Deduction
ALTER TABLE payroll.other_deductions DROP CONSTRAINT fk_other_deductions_payroll_id;
ALTER TABLE payroll.other_deductions DROP COLUMN id;
ALTER TABLE payroll.other_deductions ADD PRIMARY KEY (payroll);

ALTER TABLE payroll.other_deductions
    ADD CONSTRAINT fk_other_deductions_payroll__id
    FOREIGN KEY (payroll)
    REFERENCES payroll.payrolls(id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE payroll.other_deduction_employees
    ADD CONSTRAINT fk_other_deduction_employees_other_deduction_payroll
    FOREIGN KEY (other_deduction)
    REFERENCES payroll.other_deductions(payroll)
    ON DELETE CASCADE ON UPDATE CASCADE;