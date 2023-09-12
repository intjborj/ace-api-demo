ALTER TABLE hospital_configuration.hospital_info
ADD COLUMN IF NOT EXISTS doh_classification jsonb,
ADD COLUMN IF NOT EXISTS doh_quality_management jsonb;

