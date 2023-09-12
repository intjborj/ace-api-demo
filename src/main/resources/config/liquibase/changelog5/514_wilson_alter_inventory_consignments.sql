ALTER TABLE inventory.purchase_request ADD COLUMN consignment bool default false;
ALTER TABLE inventory.purchase_order ADD COLUMN consignment bool default false;
ALTER TABLE inventory.receiving_report ADD COLUMN consignment bool default false;