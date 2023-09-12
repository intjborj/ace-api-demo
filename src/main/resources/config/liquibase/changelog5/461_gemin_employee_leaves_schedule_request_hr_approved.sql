ALTER TABLE hrm.employee_request
    ADD COLUMN hr_approved_by                       uuid constraint fk_employee_request_hr_employee_id
                                                    references hrm.employees(id)
                                                    on update cascade on delete restrict;
