ALTER TABLE cashiering.payment_tracker
ADD COLUMN IF NOT EXISTS total_e_wallet numeric(15,2) NULL;

ALTER TABLE cashiering.payment_tracker_details
ADD COLUMN IF NOT EXISTS other_details jsonb;
