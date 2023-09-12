ALTER TABLE inventory.receiving_report_items
    ADD COLUMN input_tax numeric null,
    ADD COLUMN total_amount numeric null;