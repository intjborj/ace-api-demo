ALTER TABLE accounting.account_receivable
ADD COLUMN reference_no varchar,
ADD COLUMN due_date DATE
;

ALTER TABLE accounting.account_receivable_items
ADD COLUMN amount numeric,
ADD COLUMN discount numeric,
ADD COLUMN cwt bool,
ADD COLUMN trans_type varchar
;