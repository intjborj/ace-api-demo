CREATE TABLE IF NOT EXISTS doh.doh_logs(
    id UUID,
    type varchar,
    submitted_report text,
    report_response text,
    reporting_year int,
    status varchar,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);