CREATE TABLE IF NOT EXISTS accounting.saved_reports(
    id         uuid not null primary key,
    report_no  varchar(50),
    start_date date,
    end_date date,
    report_type varchar(20),
    reference varchar(20),
    description varchar(50),
    journal_accounts jsonb,
    json_file bytea,
    group_type varchar(20),
    amount numeric,

    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
    deleted            boolean
);
