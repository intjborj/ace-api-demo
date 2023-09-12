ALTER TABLE accounting.ar_transaction_type
DROP COLUMN journal_flag,
DROP COLUMN type,
ADD COLUMN debit varchar,
ADD COLUMN credit varchar;