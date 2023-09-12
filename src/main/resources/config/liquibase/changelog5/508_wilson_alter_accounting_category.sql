ALTER TABLE inventory.accounting_categories
ADD COLUMN IF NOT EXISTS include_department boolean default false;