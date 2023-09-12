CREATE TABLE IF NOT EXISTS fixed_assets.fixed_assets_transactions (
    id uuid primary key default uuid_generate_v4(),
    fixed_assets_item uuid,
    item_id uuid,
    amount numeric,
    transaction_type varchar,
    transaction_date timestamp(6),
    posted_ledger uuid,
    created_date timestamp(6) NULL DEFAULT now(),
    last_modified_by varchar(50) NULL,
    last_modified_date timestamp NULL DEFAULT now(),
    deleted boolean
)