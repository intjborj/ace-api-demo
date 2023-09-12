create table IF NOT EXISTS ancillary.services
(
  id                 uuid not null
    constraint service_pkey
      primary key,
  revenuecenter      uuid
    constraint fk_service_revenue_center
      references ancillary.revenue_centers
      on update cascade on delete restrict,
  servicename        varchar,
  description        varchar,
  category           varchar,
  notes              varchar,
  base_price         numeric(15, 2),
  cost               numeric(15, 2),
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
  service_code       varchar,
  department         uuid
    constraint fk_service_dept
      references public.department
      on update cascade on delete set null,
  readersfee         numeric,
  markup             numeric,
  process_code       varchar,
  deleted            boolean,
  package            boolean,
  available          boolean,
  diagnostic_service boolean
);
