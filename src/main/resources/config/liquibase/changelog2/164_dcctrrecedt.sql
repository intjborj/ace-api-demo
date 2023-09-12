ALTER TABLE cashiering.cdctr ALTER COLUMN received_datetime TYPE timestamp(0) USING received_datetime::timestamp;
