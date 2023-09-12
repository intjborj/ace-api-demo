ALTER TABLE inventory.receiving_report_items
    ADD COLUMN expiration_date date null,
    ADD COLUMN is_tax bool default true;