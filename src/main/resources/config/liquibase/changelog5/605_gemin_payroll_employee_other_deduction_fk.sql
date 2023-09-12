
ALTER TABLE hrm.employee_other_deductions
ADD CONSTRAINT fk_employee_other_deductions_employee
FOREIGN KEY (employee) REFERENCES hrm.employees (id);

ALTER TABLE hrm.employee_other_deductions
ADD CONSTRAINT fk_employee_other_deductions_other_deduction
FOREIGN KEY (other_deduction) REFERENCES hrm.other_deductions (id);
