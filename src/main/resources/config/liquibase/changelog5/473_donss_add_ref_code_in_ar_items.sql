ALTER TABLE accounting.account_receivable_items
    ADD COLUMN IF NOT EXISTS ref_code varchar;
