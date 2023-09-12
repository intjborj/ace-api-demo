ALTER TABLE accounting.bs_phil_claims
ADD COLUMN billing_id uuid,
ADD COLUMN patient_id uuid,
ADD COLUMN case_id uuid,
ADD COLUMN claim_date_created date,
ADD COLUMN claim_creator varchar,
ADD COLUMN process_date date,
DROP COLUMN billing_schedule_id,
DROP COLUMN billing_item_id;