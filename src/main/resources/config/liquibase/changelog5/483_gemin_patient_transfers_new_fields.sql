ALTER TABLE pms.cases
  ADD COLUMN opd_physician          UUID constraint fk_opd_physician_employee_id
                                    references hrm.employees(id)
                                    on update cascade on delete restrict,
  ADD COLUMN consultation           varchar,
  ADD COLUMN urgency                varchar;


ALTER TABLE pms.transfers
  ADD COLUMN opd_physician          UUID constraint fk_opd_physician_employee_id
                                    references hrm.employees(id)
                                    on update cascade on delete restrict,
  ADD COLUMN consultation           varchar,
  ADD COLUMN urgency                varchar;
