--ALTER TABLE doh.quality_managements
--ALTER COLUMN reporting_year type timestamp without time zone using to_timestamp(reporting_year) AT TIME ZONE 'UTC';