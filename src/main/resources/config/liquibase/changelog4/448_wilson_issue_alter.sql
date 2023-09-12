ALTER TABLE inventory.stock_issue
ADD COLUMN request uuid default null,
add column acct_type uuid default null,
add column posted_ledger uuid default null;