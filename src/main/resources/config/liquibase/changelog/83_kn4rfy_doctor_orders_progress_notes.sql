alter table pms.doctor_order_items
  alter column "order" type text;

create table pms.doctor_order_progress_notes
(
  id                 uuid not null
    constraint doctor_order_progress_notes_pkey
      primary key,
  entry_datetime     timestamp(6) default CURRENT_TIMESTAMP,
  note               text,
  doctor_order       uuid
    constraint fk_doctor_order_progress_notes_doctor_order
      references pms.doctor_orders
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);
