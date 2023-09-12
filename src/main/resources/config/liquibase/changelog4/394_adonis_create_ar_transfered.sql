create table accounting.ar_transfer
(
  id                             uuid not null primary key,
  company_id                     uuid,
  source_company_id              uuid,
  billing_id                     uuid,
  billing_item_id                uuid,
  transaction_id                 uuid,
  transaction_details_id         uuid,
  posted_ledger_id               uuid,
  amount                         numeric,
  status                         varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
