ALTER TABLE accounting.ar_transaction
DROP COLUMN journal_flag,
DROP COLUMN type,
ADD COLUMN debit varchar,
ADD COLUMN credit varchar;