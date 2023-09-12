create table accounting.ar_manual_deductions
(
  id                             uuid not null primary key,
  record_no                      varchar,
  company_id                     uuid,
  billing_id                     uuid,
  item_type                      varchar,
  transaction_date               timestamp(6) default CURRENT_TIMESTAMP,
  credit                         numeric,
  remarks                        varchar,
  status                         varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
