CREATE TABLE IF NOT EXISTS cashiering.payment_transaction_type(
    id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
    type_name                       varchar,
    description                     varchar,
    account                         jsonb,
    payor_type                      varchar,
    status                          varchar,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);