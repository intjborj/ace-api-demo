ALTER TABLE hrm.employee_schedule DROP CONSTRAINT fk_employee_schedule_request_id;

ALTER TABLE hrm.employee_schedule DROP COLUMN request;

ALTER TABLE hrm.employee_request
    ADD COLUMN schedule                       uuid constraint fk_employee_request_schedule_id
                                              references hrm.employee_schedule(id)
                                              on update cascade on delete restrict;
