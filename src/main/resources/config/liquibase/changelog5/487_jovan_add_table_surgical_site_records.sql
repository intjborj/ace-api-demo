CREATE TABLE IF NOT EXISTS pms.surgical_site_records(
    id UUID,
    "case" UUID,
    infections int,
    procedures int,
    remarks varchar,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);