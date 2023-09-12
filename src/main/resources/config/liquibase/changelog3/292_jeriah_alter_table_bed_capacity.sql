ALTER TABLE doh.bed_capacity
DROP COLUMN reporting_year;

ALTER TABLE doh.bed_capacity
ADD COLUMN reporting_year  int;


