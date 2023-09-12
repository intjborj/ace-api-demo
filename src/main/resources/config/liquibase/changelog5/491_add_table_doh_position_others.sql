CREATE TABLE IF NOT EXISTS referential.doh_position_others(
    id uuid not null primary key,
    poscode	int,
    postdesc varchar,
    status bool null,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);