ALTER TABLE doh.operation_minor_opt
ALTER COLUMN reporting_year type timestamp without time zone using to_timestamp(reporting_year) AT TIME ZONE 'UTC';
