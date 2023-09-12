ALTER TABLE referential.doh_icd_codes
  ADD COLUMN "deliveries" varchar,
  ADD COLUMN "cardio_respiratory_arrest" boolean;