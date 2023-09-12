
ALTER TABLE hrm.employee_request ADD PRIMARY KEY (id);

ALTER TABLE hrm.employee_schedule
    ADD COLUMN request                        uuid constraint fk_employee_schedule_request_id
                                              references hrm.employee_request(id)
                                              on update cascade on delete restrict,
    ADD COLUMN is_leave                       boolean;