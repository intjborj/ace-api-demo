ALTER TABLE cashiering.cdctr
ADD COLUMN IF NOT EXISTS status varchar default 'ACTIVE',
ADD COLUMN IF NOT EXISTS voided_by varchar,
ADD COLUMN IF NOT EXISTS voided_datetime timestamp;