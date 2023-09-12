ALTER TABLE accounting.ar_transaction
ADD COLUMN posted_ledger UUID,
DROP COLUMN balance
;