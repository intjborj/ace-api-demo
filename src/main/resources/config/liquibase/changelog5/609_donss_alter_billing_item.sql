ALTER TABLE billing.billing_item
ADD COLUMN IF NOT EXISTS approval_code varchar(50),
ADD COLUMN IF NOT EXISTS is_billed_ar bool default false;