create table accounting.ar_transaction_type
(
  id                             uuid not null primary key,
  description                    varchar,
  journal_flag                   varchar,
  type                           varchar,
  active                         BOOL,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
