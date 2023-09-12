alter table pms.cases
	add column admitting_officer uuid;

alter table pms.cases
  add constraint fk_cases_employee
    foreign key (admitting_officer)
      references hrm.employees (id)
      on update cascade on delete restrict;