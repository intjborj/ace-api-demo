create table accounting.ar_transaction
(
  id                             uuid not null primary key,
  tracking_code                  varchar,
  account_receivable_id          uuid,
  payment_tracker_id             uuid,
  trans_type_id                  uuid,
  account_id                     uuid,
  type                           varchar,
  balance                        numeric,
  amount                         numeric,
  remarks                        varchar,
  transaction_date               timestamp(6) default CURRENT_TIMESTAMP,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);

create table accounting.ar_transaction_details
(
  id                             uuid not null primary key,
  ar_transaction_id              uuid,
  account_receivable_item_id     uuid,
  type                           varchar,
  amount                         numeric,
  balance                        numeric,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
