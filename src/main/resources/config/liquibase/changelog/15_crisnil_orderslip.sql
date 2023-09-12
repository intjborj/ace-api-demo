-- auto-generated definition
create table IF NOT EXISTS ancillary.orderslips
(
  id                 uuid not null
    constraint orderslips_pkey
      primary key,
  service            uuid,
  case_number        uuid,
  doctors_order      uuid,
  created_by         varchar(50),
  created_date       timestamp(6) default now(),
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default now(),
  status             varchar,
  time_started       timestamp without time zone,
  time_completed     timestamp without time zone,
  orderslip_no       varchar,
  accession          varchar,
  posted             boolean,
  queueno            bigint,
  submitted_via_hl7  boolean,
  batchnum           varchar,
  deleted            boolean

);
