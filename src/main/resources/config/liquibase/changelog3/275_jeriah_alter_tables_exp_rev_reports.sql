ALTER TABLE doh.expenses
ALTER COLUMN reportingyear type timestamp without time zone using to_timestamp(reportingyear) AT TIME ZONE 'UTC';

ALTER TABLE doh.revenues
ALTER COLUMN reportingyear type timestamp without time zone using to_timestamp(reportingyear) AT TIME ZONE 'UTC';

ALTER TABLE doh.submitted_reports
ALTER COLUMN reporting_year type timestamp without time zone using to_timestamp(reporting_year) AT TIME ZONE 'UTC';

