ALTER TABLE accounting.disbursement DROP COLUMN releasing;
ALTER TABLE accounting.disbursement ADD COLUMN is_release bool default false;
ALTER TABLE accounting.disbursement_check ADD COLUMN releasing uuid default null;