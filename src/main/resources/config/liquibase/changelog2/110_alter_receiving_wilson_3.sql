ALTER TABLE inventory.receiving_report
    ADD COLUMN fix_discount numeric default 0,
    ADD COLUMN gross_amount numeric default 0,
    ADD COLUMN total_discount numeric default 0,
    ADD COLUMN net_of_discount numeric default 0,
    ADD COLUMN amount numeric default 0,
    ADD COLUMN vat_rate numeric default 0,
    ADD COLUMN input_tax numeric default 0,
    ADD COLUMN net_amount numeric default 0,
    ADD COLUMN vat_inclusive bool default false;