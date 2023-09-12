-- auto-generated definition
alter table hrm.employees
  add column pag_ibig_id varchar(50);

alter table pms.patients
  drop column pag_ibig_id;
