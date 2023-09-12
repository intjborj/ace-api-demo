create table if not exists accounting.account_balances
(
    id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
    description                     varchar(50),
    beg_month                       int,
    ab_year                         int,
    start_date                      date,
    end_date                        date,
    status                          varchar,
    ledger_id                       uuid,

    deleted                         BOOL,
    deleted_date                    timestamp(6) default CURRENT_TIMESTAMP,
    created_by                      varchar(50),
    created_date                    timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by                varchar(50),
    last_modified_date              timestamp(6) default CURRENT_TIMESTAMP
);