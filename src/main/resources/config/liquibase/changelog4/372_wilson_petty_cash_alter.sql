ALTER TABLE accounting.petty_cash ADD COLUMN vat_inclusive bool default false,
ADD COLUMN vat_rate numeric default 12;