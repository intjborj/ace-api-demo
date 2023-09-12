create table if not exists accounting.integration_group
(
    id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
    integration                     uuid,
    description                     varchar(50),
    deleted                         BOOL,
    deleted_date                    timestamp(6) default CURRENT_TIMESTAMP,
    created_by                      varchar(50),
    created_date                    timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by                varchar(50),
    last_modified_date              timestamp(6) default CURRENT_TIMESTAMP
);

alter table accounting.integration
add column integration_group uuid;