ALTER TABLE doh.operation_major_opt DROP COLUMN reporting_year;

ALTER TABLE doh.operation_major_opt
    ADD COLUMN reporting_year integer;