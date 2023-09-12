CREATE TABLE IF NOT EXISTS pms.central_line_records(
    id UUID,
    "case" UUID,
    start_date timestamp,
    end_date timestamp,
    sensitivity varchar,
    remarks varchar,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);