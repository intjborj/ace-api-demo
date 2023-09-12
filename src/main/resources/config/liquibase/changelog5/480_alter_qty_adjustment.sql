ALTER TABLE inventory.quantity_adjustment ADD COLUMN posted_by varchar default null,
ADD COLUMN posted_ledger uuid default null;