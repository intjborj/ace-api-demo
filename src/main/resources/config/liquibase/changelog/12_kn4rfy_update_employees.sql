drop table hrm.employees;

create table hrm.employees
(
  id                             uuid not null
    constraint employees_pkey
      primary key,
  employee_no                    varchar,
  first_name                     varchar,
  last_name                      varchar,
  middle_name                    varchar,
  name_suffix                    varchar,
  address                        varchar,
  country                        varchar,
  state_province                 varchar,
  city_municipality              varchar,
  barangay                       varchar,
  gender                         varchar,
  dob                            date,
  emergency_contact_name         varchar,
  emergency_contact_address      varchar,
  emergency_contact_relationship varchar,
  emergency_contact_no           varchar,
  "user"                         bigserial
    constraint fk_employees_user
      references public.t_user
      on update cascade on delete restrict,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);