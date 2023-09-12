create table accounting.bs_phil_claims
(
  id                             uuid not null primary key,
  billing_schedule_id            uuid,
  billing_item_id                uuid,
  case_no                        varchar,
  claim_number                   varchar,
  claim_series_lhio              varchar,
  process_stage                  varchar,
  voucher_no                     varchar,
  voucher_date                   date,
  claim_amount                   numeric,
  status                         varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
