create table accounting.ar_ledger
(
  id                             uuid not null primary key,
  ledger_no                      varchar,
  reference                      varchar,
  account_receivable             uuid,
  company                        uuid,
  personal_account               uuid,
  description                    varchar,
  debit                          numeric,
  credit                         numeric,
  balance                        numeric,
  journal_ledger                 uuid,
  ledger_date                    DATE,
  status                         varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
