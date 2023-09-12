
ALTER TABLE hrm.employee_request ALTER COLUMN status SET NOT NULL;
ALTER TABLE hrm.employee_request ALTER COLUMN reason SET NOT NULL;
ALTER TABLE hrm.employee_request ALTER COLUMN leave_type SET NOT NULL;

ALTER TABLE hrm.employee_request RENAME COLUMN leave_type TO type;

ALTER TABLE hrm.employee_request
        ADD COLUMN meal_break_start                    timestamp,
        ADD COLUMN meal_break_end                      timestamp,
        ADD COLUMN department                          uuid constraint fk_employee_request_department_id
                                                       references public.departments(id)
                                                       on update cascade on delete restrict;


ALTER TABLE hrm.employee_request ADD CONSTRAINT fk_employee_request_requested_by_id
                    FOREIGN KEY (requested_by)
                    references hrm.employees(id)
                    on update cascade on delete restrict;

ALTER TABLE hrm.employee_request ADD CONSTRAINT fk_employee_request_approved_by_id
                    FOREIGN KEY (requested_by)
                    references hrm.employees(id)
                    on update cascade on delete restrict;