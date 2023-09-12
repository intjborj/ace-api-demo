ALTER TABLE accounting.account_receivable_items
DROP COLUMN billing_id,
DROP COLUMN billing_item_id,
DROP COLUMN billing_schedule_item_id,
DROP COLUMN deleted_date,
DROP COLUMN amount,
DROP COLUMN balance,
DROP COLUMN is_voided,
ADD COLUMN description varchar,
ADD COLUMN debit numeric,
ADD COLUMN credit numeric
;