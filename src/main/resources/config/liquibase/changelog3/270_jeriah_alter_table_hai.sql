ALTER TABLE doh.operation_hai
ALTER COLUMN reportingyear type timestamp without time zone using to_timestamp(reportingyear) AT TIME ZONE 'UTC';
