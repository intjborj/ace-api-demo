ALTER TABLE accounting.header_ledger
ADD COLUMN IF NOT EXISTS transaction_date_only date;