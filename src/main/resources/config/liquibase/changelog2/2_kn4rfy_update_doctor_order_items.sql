drop table if exists "pms"."doctor_order_items";
drop table if exists "pms"."doctor_order_progress_notes";
drop table if exists "pms"."doctor_orders";

create table pms.doctor_orders
(
  id                 uuid not null
    constraint doctor_orders_pkey
      primary key,
  entry_datetime     timestamp(6) default CURRENT_TIMESTAMP,
  employee           uuid,
  "case"             uuid
    constraint fk_doctor_orders_cases
      references pms.cases
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);

create table pms.doctor_order_items
(
  id                      uuid not null
    constraint doctor_order_items_pkey
      primary key,
  entry_datetime          timestamp(6) default CURRENT_TIMESTAMP,
  "order"                 text,
  type                    varchar(20),
  status                  varchar(10)  default 'PENDING',
  action                  varchar(20),
  volume                  varchar(20),
  flow_rate               varchar(20),
  medicine                uuid,
  additive                uuid,
  medication_type         varchar(20),
  frequency               varchar(20),
  dose                    varchar(20),
  route                   varchar(20),
  service                 uuid,
  additional_instructions text,
  doctor_order            uuid
    constraint fk_doctor_order_items_doctor_orders
      references pms.doctor_orders
      on update cascade on delete restrict,
  created_by              varchar(50),
  created_date            timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by        varchar(50),
  last_modified_date      timestamp(6) default CURRENT_TIMESTAMP
);

create table pms.doctor_order_progress_notes
(
  id                 uuid not null
    constraint doctor_order_progress_notes_pkey
      primary key,
  entry_datetime     timestamp(6) default CURRENT_TIMESTAMP,
  note               text,
  doctor_order       uuid
    constraint fk_doctor_order_progress_notes_doctor_orders
      references pms.doctor_orders
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);