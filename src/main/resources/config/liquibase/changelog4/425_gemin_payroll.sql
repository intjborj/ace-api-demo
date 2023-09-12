CREATE TABLE "hrm"."payrolls" (
    id                                   uuid NOT NULL PRIMARY KEY,
    name                                 varchar,
    generated                            bool,
    generated_date                       timestamp,
    date_start                           timestamp,
    date_end                             timestamp,
    note                                 varchar,
    status                               varchar,
    salary_rate_multiplier               uuid,
    locked_date                          timestamp,
    created_by                           varchar(50),
    created_date                         timestamp DEFAULT now(),
    last_modified_by                     varchar(50),
    last_modified_date                   timestamp DEFAULT now(),
    deleted                              bool
);

CREATE TABLE "hrm"."payslips" (
    id                                                uuid NOT NULL PRIMARY KEY,
    payroll                                           uuid NOT NULL
                                                             constraint fk_payslip_payroll_id
                                                             references hrm.payrolls(id)
                                                             on update cascade on delete restrict,
    employee                                          uuid NOT NULL
                                                             constraint fk_payslip_employee_id
                                                             references hrm.employees(id)
                                                             on update cascade on delete restrict,
    basic_salary                                      numeric,
    hours_late                                        numeric,
    hours_absent                                      numeric,
    deduct_sss                                        numeric DEFAULT 0,
    deduct_gsis                                       numeric DEFAULT 0,
    deduct_philhealth                                 numeric DEFAULT 0,
    deduct_hdmf                                       numeric DEFAULT 0,
    deduct_others                                     numeric DEFAULT 0,
    deduct_cash_advance                               numeric DEFAULT 0,
    withholding_tax                                   numeric DEFAULT 0,
    include_in_payroll                                bool,
    hours_regular_overtime                            numeric,
    hours_restday_overtime                            numeric,
    hours_special_holiday_overtime                    numeric,
    hours_special_holiday_and_rest_day_overtime       numeric,
    hours_regular_holiday_overtime                    numeric,
    hours_regular_holiday_and_rest_day_overtime       numeric,
    hours_double_holiday_overtime                     numeric,
    hours_double_holiday_and_rest_day_overtime        numeric,
    hours_regular                                     numeric,
    hours_restday                                     numeric,
    hours_special_holiday                             numeric,
    hours_special_holiday_and_rest_day                numeric,
    hours_regular_holiday                             numeric,
    hours_regular_holiday_and_rest_day                numeric,
    hours_double_holiday                              numeric,
    hours_double_holiday_and_rest_day                 numeric,
    adjustment                                        numeric,
    adjustment_reason                                 varchar,
    deduct_sss_employer                               numeric DEFAULT 0,
    deduct_gsis_employer                              numeric DEFAULT 0,
    deduct_philhealth_employer                        numeric DEFAULT 0,
    deduct_hdmf_employer                              numeric DEFAULT 0,
    created_by                                        varchar(50),
    created_date                                      timestamp DEFAULT now(),
    last_modified_by                                  varchar(50),
    last_modified_date                                timestamp DEFAULT now(),
    deleted                                           bool
);