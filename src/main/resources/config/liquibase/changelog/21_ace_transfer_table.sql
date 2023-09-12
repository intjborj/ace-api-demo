create table pms.transfers
(
  id                 uuid not null primary key,

  registry_type      varchar,
  "department"       uuid
    constraint fk_transfers_departments
      references public.departments
      on update cascade on delete restrict,
  case_no            varchar,
  "room"       uuid
    constraint fk_transfers_rooms
      references bms.rooms
      on update cascade on delete restrict,
  status             varchar,

  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);