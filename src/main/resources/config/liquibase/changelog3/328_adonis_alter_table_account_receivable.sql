ALTER TABLE accounting.account_receivable
ADD COLUMN patient_type varchar,
ADD COLUMN posted_ledger uuid;

ALTER TABLE accounting.billing_schedule
ADD COLUMN patient_type varchar;