ALTER TABLE accounting.debit_memo DROP COLUMN net_amount;
ALTER TABLE accounting.debit_memo ADD COLUMN applied_amount numeric default 0.00;