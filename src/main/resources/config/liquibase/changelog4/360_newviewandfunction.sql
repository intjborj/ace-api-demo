-- FUNCTION: pms.double_get_hours(timestamp without time zone, timestamp without time zone)

-- DROP FUNCTION pms.double_get_hours(timestamp without time zone, timestamp without time zone);

CREATE OR REPLACE FUNCTION pms.double_get_hours(
	start_datetime timestamp without time zone,
	end_datetime timestamp without time zone)
    RETURNS double precision
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE

AS $BODY$
declare
	hours int;
begin
	return (EXTRACT(EPOCH FROM end_datetime + '08:00:00'::interval) - EXTRACT(EPOCH FROM start_datetime  + '08:00:00'::interval))/3600;
end;
$BODY$;

-- View: pms.total_deaths

-- DROP VIEW pms.total_deaths;

CREATE OR REPLACE VIEW pms.total_deaths
 AS
 SELECT count(*) FILTER (WHERE pms.double_get_hours(cases.admission_datetime, cases.discharged_datetime) > 48::double precision) AS greater_than_48hrs,
    count(*) FILTER (WHERE pms.double_get_hours(cases.admission_datetime, cases.discharged_datetime) < 48::double precision) AS less_than_48hrs,
    count(*) FILTER (WHERE pms.double_get_hours(cases.admission_datetime, cases.discharged_datetime) > 48::double precision)::integer + count(*) FILTER (WHERE pms.double_get_hours(cases.admission_datetime, cases.discharged_datetime) < 48::double precision)::integer AS total
   FROM pms.cases
  WHERE cases.discharge_condition::text = 'EXPIRED'::text AND cases.admission_datetime > to_date('07/15/2020'::text, 'MM/DD/YYYY'::text);




