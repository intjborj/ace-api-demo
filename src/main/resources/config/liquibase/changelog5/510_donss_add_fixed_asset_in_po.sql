ALTER TABLE inventory.purchase_order
ADD COLUMN IF NOT EXISTS is_fixed_asset BOOLEAN default false;