alter table hrm.employees
  add constraint fk_employees_department
    foreign key (department)
      references public.departments
      on update cascade on delete restrict;
