
ALTER TABLE hrm.employee_request
    ADD COLUMN IF NOT EXISTS approved_date                timestamp,
    ADD COLUMN IF NOT EXISTS hr_approved_date             timestamp,
    ADD COLUMN IF NOT EXISTS requested_date                 timestamp;

