ALTER TABLE accounting.ar_transaction_type
RENAME COLUMN deleted TO is_voided;

ALTER TABLE accounting.ar_transaction_details
RENAME COLUMN deleted TO is_voided;

ALTER TABLE accounting.account_receivable
RENAME COLUMN deleted TO is_voided;

ALTER TABLE accounting.account_receivable_items
RENAME COLUMN deleted TO is_voided;

ALTER TABLE accounting.billing_schedule
RENAME COLUMN deleted TO is_voided;

ALTER TABLE accounting.billing_schedule_items
RENAME COLUMN deleted TO is_voided;