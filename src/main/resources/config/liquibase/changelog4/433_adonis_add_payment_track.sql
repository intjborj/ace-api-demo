create table accounting.ar_payment_tracker
(
  id                                uuid not null primary key,
  payment_tracker_id                uuid,
  or_number                         varchar,
  amount                            numeric,
  credit                            numeric,
  balance                           numeric,
  status                            varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);

create table accounting.ar_payment_tracker_trans
(
  id                                uuid not null primary key,
  ar_payment_tracker_id             uuid,
  account_receivable_id             uuid,
  ar_transaction_id                 uuid,
  amount                            numeric,
  status                            varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
