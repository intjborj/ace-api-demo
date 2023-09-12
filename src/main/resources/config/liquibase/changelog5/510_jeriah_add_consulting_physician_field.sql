ALTER TABLE "pms"."cases"
      ADD COLUMN consulting_physician       UUID
                constraint fk_consulting_physician_employees_id
                references hrm.employees(id)
                on update cascade on delete restrict;

ALTER TABLE "pms"."cases"
      ADD COLUMN consulting_physician_trs       UUID
                      constraint fk_consulting_physician_trs_employees_id
                      references hrm.employees(id)
                      on update cascade on delete restrict;