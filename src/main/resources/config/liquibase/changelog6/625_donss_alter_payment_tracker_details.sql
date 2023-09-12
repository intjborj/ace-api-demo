ALTER TABLE cashiering.payment_tracker_details
ADD COLUMN IF NOT EXISTS deposit_date timestamp null,
ADD COLUMN IF NOT EXISTS status varchar null;
