ALTER TABLE accounting.payables ADD COLUMN da_amount numeric default 0.00,
ADD COLUMN dm_amount numeric default 0.00,
ADD COLUMN dm_ref_no varchar default NULL;

ALTER TABLE accounting.disbursement_ap ADD COLUMN debit_memo uuid default null;

