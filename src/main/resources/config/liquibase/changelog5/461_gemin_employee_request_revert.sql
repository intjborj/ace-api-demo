
ALTER TABLE hrm.employee_request
    ADD COLUMN IF NOT EXISTS reverted_by                  UUID CONSTRAINT fk_employee_request_reverted_employee_id
                                            REFERENCES hrm.employees(id)
                                            ON UPDATE CASCADE ON DELETE RESTRICT,
    ADD COLUMN IF NOT EXISTS reverted_date                timestamp;

