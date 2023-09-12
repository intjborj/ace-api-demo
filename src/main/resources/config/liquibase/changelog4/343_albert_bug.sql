ALTER TABLE cashiering.collection_detail ALTER COLUMN collection TYPE uuid USING collection::uuid;
