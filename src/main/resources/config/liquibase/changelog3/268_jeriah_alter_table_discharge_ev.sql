ALTER TABLE doh.discharge_ev
ALTER COLUMN reporting_year type timestamp without time zone using to_timestamp(reporting_year) AT TIME ZONE 'UTC';
