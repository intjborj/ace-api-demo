create table IF NOT EXISTS ancillary.revenue_centers
(
  id                 uuid not null
    constraint revenue_centers_pkey
      primary key,
  code               varchar,
  name               varchar,
  description        varchar,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
  department         uuid
    constraint fk_service_dept
      references public.department
      on update cascade on delete set null
);

