CREATE TABLE "hrm"."log_flags" (
    id                                                uuid NOT NULL PRIMARY KEY,
    payroll                                           uuid NOT NULL
                                                             constraint fk_payslip_payroll_id
                                                             references hrm.payrolls(id)
                                                             on update cascade on delete restrict,
    employee                                          uuid NOT NULL
                                                             constraint fk_payslip_employee_id
                                                             references hrm.employees(id)
                                                             on update cascade on delete restrict,
    status                                            varchar,
    date                                              timestamp,

    created_by                                        varchar(50),
    created_date                                      timestamp DEFAULT now(),
    last_modified_by                                  varchar(50),
    last_modified_date                                timestamp DEFAULT now(),
    deleted                                           bool
);