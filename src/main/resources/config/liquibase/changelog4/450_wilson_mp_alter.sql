ALTER TABLE inventory.material_production
ADD COLUMN produced_by uuid default null,
add column acct_type uuid default null,
add column posted_ledger uuid default null;