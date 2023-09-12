ALTER TABLE hospital_configuration.operational_configuration
ADD COLUMN IF NOT EXISTS contact_numbers jsonb null;
