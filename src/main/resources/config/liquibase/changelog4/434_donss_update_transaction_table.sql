ALTER TABLE accounting.ar_transaction_type
ADD COLUMN journal_accounts TEXT,
DROP COLUMN debit,
DROP COLUMN credit;