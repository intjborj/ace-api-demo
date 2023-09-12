ALTER TABLE fixed_assets.fixed_asset_items
ADD COLUMN IF NOT EXISTS unit_of_time varchar(50),
ADD COLUMN IF NOT EXISTS depreciation_date_start timestamp(6) default now(),
ADD COLUMN IF NOT EXISTS categories UUID;

ALTER TABLE fixed_assets.fixed_asset_depreciation
ADD COLUMN IF NOT EXISTS unit_of_time varchar(50),
ADD COLUMN IF NOT EXISTS depreciation_date_start timestamp(6) default now();