
ALTER TABLE hrm.employee_request
    DROP COLUMN schedule;

ALTER TABLE hrm.employee_request
    DROP COLUMN meal_break_start;

ALTER TABLE hrm.employee_request
    DROP COLUMN start_datetime;

ALTER TABLE hrm.employee_request
    DROP COLUMN end_datetime;

ALTER TABLE hrm.employee_request
    DROP COLUMN meal_break_end;



ALTER TABLE hrm.employee_request
    ADD COLUMN dates jsonb;

ALTER TABLE hrm.employee_request
    ADD COLUMN dates_type varchar;

ALTER TABLE hrm.employee_schedule
    ADD COLUMN request           uuid constraint fk_employee_schedule_request_id
                                 references hrm.employee_request(id)
                                 on update cascade on delete restrict;


