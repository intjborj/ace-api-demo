ALTER TABLE accounting.transaction_type ADD COLUMN asset bool default false;
ALTER TABLE accounting.transaction_type ADD COLUMN consignment bool default false;