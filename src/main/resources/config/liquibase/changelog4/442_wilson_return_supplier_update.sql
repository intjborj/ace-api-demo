ALTER TABLE inventory.return_supplier add column return_by varchar default null,
add column return_user uuid default null,
add column acct_type uuid default null,
add column posted_ledger uuid default null;