CREATE OR REPLACE VIEW "pms"."inpatient_report_mrd" AS  SELECT pt.patient_no AS "Case Number",
    to_char(cs.admission_datetime + '08:00:00'::interval, 'MM/DD/YYYY hh12:mi AM'::text) AS "Admission Date and Time",
    concat(pt.first_name, ' ', pt.name_suffix) AS "Firstname",
    pt.middle_name AS "Middlename",
    pt.last_name AS "Lastname",
    to_char(pt.dob::timestamp with time zone, 'MM/DD/YYYY'::text) AS "Birth Date",
    date_part('year'::text, age(cs.admission_datetime::date::timestamp with time zone, pt.dob::timestamp with time zone)) AS "Age",
    pt.gender AS "Gender",
    ltrim(concat(pt.address, ' ', pt.barangay, ' ', pt.city_municipality)) AS "Address",
        CASE
            WHEN cs.accommodation_type::text ~~ 'SELF'::text THEN 'NN'::text
            WHEN cs.accommodation_type::text ~~ 'NHIP%'::text THEN 'NH'::text
            ELSE NULL::text
        END AS "Membership",
    cs.admitting_diagnosis AS "Admitting Diagnosis",
    ( SELECT ((hrmemp.first_name::text || ', '::text) || (hrmemp.middle_name::text || ', '::text)) || hrmemp.last_name::text AS admitting_physician
           FROM hrm.employees hrmemp
          WHERE hrmemp.id = cs.admitting_physician) AS "Admitting Physician",
    ((emp.first_name::text || ', '::text) || (emp.middle_name::text || ', '::text)) || emp.last_name::text AS "Attending Physician",
    to_char(cs.discharged_datetime + '08:00:00'::interval, 'MM/DD/YYYY hh12:mi AM'::text) AS "Discharge Date and Time",
    concat(cs.discharge_disposition, '/', cs.discharge_condition) AS "Disposition",
    cs.discharge_diagnosis AS "Final Diagnosis",

    to_char(cs.admission_datetime + '08:00:00'::interval, 'MM/DD/YYYY'::text) AS "Admission Date"
   FROM pms.cases cs
     LEFT JOIN pms.patients pt ON pt.id = cs.patient
     LEFT JOIN hrm.employees emp ON emp.id = cs.attending_physician
  WHERE cs.registry_type::text = 'IPD'::text
  ORDER BY cs.admission_datetime;

ALTER TABLE "pms"."inpatient_report_mrd" OWNER TO "postgres";