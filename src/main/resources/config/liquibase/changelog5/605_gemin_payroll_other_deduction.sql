create table if not exists payroll.other_deductions(
    id                                                uuid PRIMARY KEY default uuid_generate_v4(),
    payroll                                           uuid NOT NULL
                                                           CONSTRAINT fk_other_deductions_payroll_id
                                                           REFERENCES payroll.payrolls(id)
                                                           ON UPDATE CASCADE ON DELETE RESTRICT,
    status                                            varchar,

    created_by                                        varchar(50),
    created_date                                      timestamp DEFAULT now(),
    last_modified_by                                  varchar(50),
    last_modified_date                                timestamp DEFAULT now(),
    deleted                                           bool
);

CREATE TABLE IF NOT EXISTS payroll.other_deduction_employees(
    id                                                uuid PRIMARY KEY default uuid_generate_v4(),
    other_deduction                                   uuid NOT NULL
                                                           CONSTRAINT fk_other_deduction_employee_other_deduction
                                                           REFERENCES payroll.other_deductions(id)
                                                           ON UPDATE CASCADE ON DELETE RESTRICT,
    employees                                         uuid NOT NULL
                                                           CONSTRAINT fk_other_deduction_employee_employee
                                                           REFERENCES hrm.employees(id)
                                                           ON UPDATE CASCADE ON DELETE RESTRICT,
    status                                            varchar,

    created_by                                        varchar(50),
    created_date                                      timestamp DEFAULT now(),
    last_modified_by                                  varchar(50),
    last_modified_date                                timestamp DEFAULT now(),
    deleted                                           bool
);

CREATE TABLE IF NOT EXISTS payroll.other_deduction_employees_item(
    id                                                uuid PRIMARY KEY default uuid_generate_v4(),
    other_deduction_employee                          uuid NOT NULL
                                                           CONSTRAINT fk_other_deduction_employees_item_other_deduction_employees
                                                           REFERENCES payroll.other_deduction_employees(id)
                                                           ON UPDATE CASCADE ON DELETE RESTRICT,
    title                                             varchar,
    identifier                                        varchar,
    amount                                            numeric(15,2),

    created_by                                        varchar(50),
    created_date                                      timestamp DEFAULT now(),
    last_modified_by                                  varchar(50),
    last_modified_date                                timestamp DEFAULT now(),
    deleted                                           bool
);

CREATE TABLE IF NOT EXISTS hrm.other_deductions(
    id                                                uuid PRIMARY KEY default uuid_generate_v4(),
    title                                             varchar,
    identifier                                        varchar,
    amount                                            numeric(15,2),
    active                                            boolean,

    created_by                                        varchar(50),
    created_date                                      timestamp DEFAULT now(),
    last_modified_by                                  varchar(50),
    last_modified_date                                timestamp DEFAULT now(),
    deleted                                           bool
);

CREATE TABLE IF NOT EXISTS hrm.employee_other_deductions(
    employee                                          uuid NOT NULL
                                                           CONSTRAINT fk_employee_other_deductions_employee
                                                           REFERENCES hrm.employees(id)
                                                           ON UPDATE CASCADE ON DELETE RESTRICT,
    other_deduction                                   uuid NOT NULL
                                                           CONSTRAINT fk_employee_other_deductions_other_deduction
                                                           REFERENCES hrm.other_deductions(id)
                                                           ON UPDATE CASCADE ON DELETE RESTRICT
);


