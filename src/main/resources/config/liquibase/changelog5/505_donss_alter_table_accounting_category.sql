ALTER TABLE inventory.accounting_categories
ADD COLUMN IF NOT EXISTS is_fixed_asset boolean default false;