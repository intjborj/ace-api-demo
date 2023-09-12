ALTER TABLE doh.expenses
DROP COLUMN reportingyear;

ALTER TABLE doh.expenses
ADD COLUMN reporting_year int;

ALTER TABLE doh.revenues
DROP COLUMN reportingyear;

ALTER TABLE doh.revenues
ADD COLUMN reporting_year int;

ALTER TABLE doh.submitted_reports
DROP COLUMN reporting_year;

ALTER TABLE doh.submitted_reports
ADD COLUMN reporting_year int;



