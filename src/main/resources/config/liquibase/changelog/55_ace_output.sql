create table pms.outputs
(
  id                 uuid not null
    constraint output_pkey
      primary key,
  entry_datetime     timestamp    default CURRENT_TIMESTAMP,
  voided_output          varchar,
  catheter_output        varchar,
  ng_output         varchar,
  insensible_loss_output       varchar,
  emesis_output         varchar,
  stool_output          varchar,
  "case"             uuid
    constraint fk_intakes_cases
      references pms.cases
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);