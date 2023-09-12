ALTER TABLE accounting.ar_transaction_details
ADD COLUMN reference_no varchar,
ADD COLUMN billing_item_ref UUID;