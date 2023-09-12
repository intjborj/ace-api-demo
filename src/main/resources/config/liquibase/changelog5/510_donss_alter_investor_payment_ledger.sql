ALTER TABLE billing.investor_payment_ledger
ADD COLUMN is_voided bool default false,
ADD COLUMN payment_ledger_ref_id uuid;