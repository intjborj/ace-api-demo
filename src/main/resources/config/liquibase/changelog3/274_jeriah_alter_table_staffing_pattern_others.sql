ALTER TABLE doh.staffing_pattern_others
ALTER COLUMN reportingyear type timestamp without time zone using to_timestamp(reportingyear) AT TIME ZONE 'UTC';
