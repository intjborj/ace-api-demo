
 WITH patient_age AS (
         SELECT p_1.id,
            p_1.gender,
            date_part('year'::text, age(c_1.entry_datetime::timestamp with time zone, p_1.dob::timestamp with time zone))::integer AS page,
            c_1.discharge_disposition
           FROM pms.patients p_1
             LEFT JOIN pms.cases c_1 ON c_1.patient = p_1.id
          WHERE c_1.id IS NOT NULL AND c_1.discharge_disposition IS NOT NULL AND c_1.discharge_condition::text = 'EXPIRED'::text
        )
 SELECT d.value ->> 'icdCode'::text AS icdcode,
    d.value ->> 'icdDesc'::text AS longname,
    date_part('year'::text, c.entry_datetime) AS reporting_year,
    count(*) FILTER (WHERE p.page < 1 AND p.gender::text = 'MALE'::text) AS munder1,
    count(*) FILTER (WHERE p.page < 1 AND p.gender::text = 'FEMALE'::text) AS funder1,
    count(*) FILTER (WHERE p.page >= 1 AND p.page <= 4 AND p.gender::text = 'MALE'::text) AS m1to4,
    count(*) FILTER (WHERE p.page >= 1 AND p.page <= 4 AND p.gender::text = 'FEMALE'::text) AS f1to4,
    count(*) FILTER (WHERE p.page >= 5 AND p.page <= 9 AND p.gender::text = 'MALE'::text) AS m5to9,
    count(*) FILTER (WHERE p.page >= 5 AND p.page <= 9 AND p.gender::text = 'FEMALE'::text) AS f5to9,
    count(*) FILTER (WHERE p.page >= 10 AND p.page <= 14 AND p.gender::text = 'MALE'::text) AS m10to14,
    count(*) FILTER (WHERE p.page >= 10 AND p.page <= 14 AND p.gender::text = 'FEMALE'::text) AS f10to14,
    count(*) FILTER (WHERE p.page >= 15 AND p.page <= 19 AND p.gender::text = 'MALE'::text) AS m15to19,
    count(*) FILTER (WHERE p.page >= 15 AND p.page <= 19 AND p.gender::text = 'FEMALE'::text) AS f15to19,
    count(*) FILTER (WHERE p.page >= 20 AND p.page <= 24 AND p.gender::text = 'MALE'::text) AS m20to24,
    count(*) FILTER (WHERE p.page >= 20 AND p.page <= 24 AND p.gender::text = 'FEMALE'::text) AS f20to24,
    count(*) FILTER (WHERE p.page >= 25 AND p.page <= 29 AND p.gender::text = 'MALE'::text) AS m25to29,
    count(*) FILTER (WHERE p.page >= 25 AND p.page <= 29 AND p.gender::text = 'FEMALE'::text) AS f25to29,
    count(*) FILTER (WHERE p.page >= 30 AND p.page <= 34 AND p.gender::text = 'MALE'::text) AS m30to34,
    count(*) FILTER (WHERE p.page >= 30 AND p.page <= 34 AND p.gender::text = 'FEMALE'::text) AS f30to34,
    count(*) FILTER (WHERE p.page >= 35 AND p.page <= 39 AND p.gender::text = 'MALE'::text) AS m35to39,
    count(*) FILTER (WHERE p.page >= 35 AND p.page <= 39 AND p.gender::text = 'FEMALE'::text) AS f35to39,
    count(*) FILTER (WHERE p.page >= 40 AND p.page <= 44 AND p.gender::text = 'MALE'::text) AS m40to44,
    count(*) FILTER (WHERE p.page >= 40 AND p.page <= 44 AND p.gender::text = 'FEMALE'::text) AS f40to44,
    count(*) FILTER (WHERE p.page >= 45 AND p.page <= 49 AND p.gender::text = 'MALE'::text) AS m45to49,
    count(*) FILTER (WHERE p.page >= 45 AND p.page <= 49 AND p.gender::text = 'FEMALE'::text) AS f45to49,
    count(*) FILTER (WHERE p.page >= 50 AND p.page <= 54 AND p.gender::text = 'MALE'::text) AS m50to54,
    count(*) FILTER (WHERE p.page >= 50 AND p.page <= 54 AND p.gender::text = 'FEMALE'::text) AS f50to54,
    count(*) FILTER (WHERE p.page >= 55 AND p.page <= 59 AND p.gender::text = 'MALE'::text) AS m55to59,
    count(*) FILTER (WHERE p.page >= 55 AND p.page <= 59 AND p.gender::text = 'FEMALE'::text) AS f55to59,
    count(*) FILTER (WHERE p.page >= 60 AND p.page <= 64 AND p.gender::text = 'MALE'::text) AS m60to64,
    count(*) FILTER (WHERE p.page >= 60 AND p.page <= 64 AND p.gender::text = 'FEMALE'::text) AS f60to64,
    count(*) FILTER (WHERE p.page >= 65 AND p.page <= 69 AND p.gender::text = 'MALE'::text) AS m65to69,
    count(*) FILTER (WHERE p.page >= 65 AND p.page <= 69 AND p.gender::text = 'FEMALE'::text) AS f65to69,
    count(*) FILTER (WHERE p.page >= 70 AND p.gender::text = 'MALE'::text) AS m70over,
    count(*) FILTER (WHERE p.page >= 70 AND p.gender::text = 'FEMALE'::text) AS f70over,
    count(*) FILTER (WHERE p.gender::text = 'MALE'::text) AS msubtotal,
    count(*) FILTER (WHERE p.gender::text = 'FEMALE'::text) AS fsubtotal,
    count(*) AS total
   FROM pms.cases c,
    patient_age p,
    LATERAL json_array_elements(
        CASE
            WHEN pms.is_json(c.doh_icd_diagnosis::character varying) AND c.doh_icd_diagnosis IS NOT NULL THEN c.doh_icd_diagnosis::json
            ELSE NULL::json
        END) d(value)
        LEFT JOIN referential.doh_icd_codes icd on icd.icd10_code = d.value ->> 'icdCode'::text
  WHERE c.patient = p.id AND c.discharge_condition::text = 'EXPIRED'::text and d.value ->> 'icdTag'::text = 'Underlying-Primary' and  COALESCE(icd.cardio_respiratory_arrest, FALSE) = FALSE
  GROUP BY (d.value ->> 'icdCode'::text), (d.value ->> 'icdDesc'::text), (date_part('year'::text, c.entry_datetime))
  ORDER BY (count(*)) DESC;

