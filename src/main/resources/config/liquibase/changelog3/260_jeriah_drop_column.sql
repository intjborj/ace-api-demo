ALTER TABLE doh.discharge_opv
ALTER COLUMN reporting_year type timestamp without time zone using to_timestamp(reporting_year) AT TIME ZONE 'UTC';
