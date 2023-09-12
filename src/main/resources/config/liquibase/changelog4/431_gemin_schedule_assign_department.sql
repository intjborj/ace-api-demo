alter table "hrm"."employee_schedule"
add column department                     uuid
                                          constraint fk_employee_schedule_department_id
                                          references departments(id)
                                          on update cascade on delete restrict;

