
ALTER TABLE hrm.employee_request
    DROP COLUMN IF EXISTS approved_by;

CREATE TABLE IF NOT EXISTS hrm.employee_request_approval (
    id                                      UUID PRIMARY KEY,
    request                                 UUID CONSTRAINT fk_employee_request_approval_employee_request_id
                                            REFERENCES hrm.employee_request(id)
                                            ON UPDATE CASCADE ON DELETE RESTRICT,
    employee                                UUID CONSTRAINT fk_employee_request_approval_employee_id
                                            REFERENCES hrm.employees(id)
                                            ON UPDATE CASCADE ON DELETE RESTRICT,
    status                                  varchar(50),
    remarks                                 TEXT,

    created_by                                        varchar(50),
    created_date                                      timestamp DEFAULT now(),
    last_modified_by                                  varchar(50),
    last_modified_date                                timestamp DEFAULT now(),
    deleted                                           bool
);