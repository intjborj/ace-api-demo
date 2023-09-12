ALTER TABLE inventory.supplier ADD COLUMN ewt_rate numeric default 0.10;
update inventory.supplier set is_vatable = true, is_vat_inclusive = true;
update inventory.supplier set ewt_rate = 0.01 where employee_id is null;