create table pms.doctor_order
(
  id                 uuid not null
    constraint doctor_order_pkey
      primary key,
  entry_datetime     timestamp(6) default CURRENT_TIMESTAMP,
  employee           uuid,
  "case"             uuid
    constraint fk_doctor_order_cases
      references pms.cases
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);

create table pms.doctor_order_item
(
  id                 uuid not null
    constraint doctor_order_item_pkey
      primary key,
  entry_datetime     timestamp(6) default CURRENT_TIMESTAMP,
  "order"            varchar(255),
  type               varchar(10),
  status             varchar(10) default 'PENDING',
  doctor_order       uuid
    constraint fk_doctor_order_item_doctor_order
      references pms.doctor_order
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);
