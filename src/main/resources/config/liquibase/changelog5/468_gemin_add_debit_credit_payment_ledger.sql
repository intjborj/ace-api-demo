
ALTER TABLE billing.investor_payment_ledger
    ADD COLUMN debit                             numeric(15,4),
    ADD COLUMN credit                            numeric(15,4);


ALTER TABLE billing.investor_payment_ledger
    DROP COLUMN amount;
