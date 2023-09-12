ALTER TABLE inventory.physical_count_transactions ALTER COLUMN trans_date TYPE timestamp USING trans_date::timestamp;
