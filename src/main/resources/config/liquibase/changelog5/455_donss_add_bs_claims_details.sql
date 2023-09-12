create table accounting.bs_phil_claims_items
(
  id                             uuid not null primary key,
  bs_claim_id                    uuid,
  billing_id                     uuid,
  billing_item_id                uuid,
  patient_id                     uuid,
  case_id                        uuid,
  receivable_id                  uuid,
  receivable_item_id             uuid,
  type                           varchar,
  amount                         numeric,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
