CREATE TABLE IF NOT EXISTS accounting.ar_customers(
    id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
    account_prefix   varchar,
    account_no   varchar,
    name         varchar,
    address      varchar,
    type         varchar,
    discount_and_penalties   jsonb null,
    reference_id uuid,
    patient_id uuid,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);

CREATE INDEX ar_customers_ref_id_index ON accounting.ar_customers USING btree (reference_id);