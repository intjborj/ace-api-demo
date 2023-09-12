ALTER TABLE accounting.disbursement_ap ADD COLUMN vat_rate numeric default 0,
ADD COLUMN vat_inclusive bool default false,
ADD COLUMN vat_amount numeric default 0;