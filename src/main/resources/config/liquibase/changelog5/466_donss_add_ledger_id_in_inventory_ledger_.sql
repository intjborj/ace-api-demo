ALTER TABLE inventory.inventory_ledger
ADD COLUMN posted_ledger uuid,
ADD COLUMN canceled_ref uuid;