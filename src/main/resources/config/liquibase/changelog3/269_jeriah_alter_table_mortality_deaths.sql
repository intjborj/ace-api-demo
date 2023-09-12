ALTER TABLE doh.mortality_death
ALTER COLUMN reportingyear type timestamp without time zone using to_timestamp(reportingyear) AT TIME ZONE 'UTC';
