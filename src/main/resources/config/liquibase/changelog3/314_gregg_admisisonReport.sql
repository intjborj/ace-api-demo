CREATE OR REPLACE VIEW "pms"."inpatient_report_mamcyra" AS  SELECT pt.patient_no,
    cs.case_no AS "Case Number",
    to_char((cs.admission_datetime + '08:00:00'::interval), 'MM/DD/YYYY'::text) AS "Admission Date",
    to_char((cs.admission_datetime + '08:00:00'::interval), 'hh12:mi AM'::text) AS "Admission Time",
    to_char((cs.admission_datetime + '08:00:00'::interval), 'MM/DD/YYYY hh12:mi AM'::text) AS "Admission Date and Time",
    pt.first_name AS "Firstname",
    pt.middle_name AS "Middlename",
    pt.last_name AS "Lastname",
    pt.name_suffix AS "Suffix",
    to_char((pt.dob)::timestamp with time zone, 'YYYY'::text) AS "Year",
    to_char((pt.dob)::timestamp with time zone, 'MM'::text) AS "Month",
    to_char((pt.dob)::timestamp with time zone, 'DD'::text) AS "Day",
    to_char((pt.dob)::timestamp with time zone, 'MM/DD/YYYY'::text) AS "Birth Date",
    date_part('year'::text, age(((cs.admission_datetime)::date)::timestamp with time zone, (pt.dob)::timestamp with time zone)) AS "Age",
    date_part('year'::text, age(((cs.admission_datetime)::date)::timestamp with time zone, (pt.dob)::timestamp with time zone)) AS "Age_Years",
    date_part('month'::text, age(((cs.admission_datetime)::date)::timestamp with time zone, (pt.dob)::timestamp with time zone)) AS "Age_Month",
    date_part('day'::text, age(((cs.admission_datetime)::date)::timestamp with time zone, (pt.dob)::timestamp with time zone)) AS "Age_Day",
    pt.gender AS "Gender",
    ltrim(concat(pt.address, ' ', pt.barangay, ' ', pt.city_municipality, ' ', pt.state_province, ' ', pt.country, ' ', pt.zip_code, ' ')) AS "Address",
    cs.accommodation_type AS "Membership",
    cs.admitting_diagnosis AS "Admitting Diagnosis",
    ( SELECT (((hrmemp.first_name)::text || ' '::text) || (hrmemp.last_name)::text) AS admitting_physician
           FROM hrm.employees hrmemp
          WHERE (hrmemp.id = cs.admitting_physician)) AS "Admitting Physician",
    (((emp.first_name)::text || ' '::text) || (emp.last_name)::text) AS "Attending Physician",
    to_char((cs.discharged_datetime + '08:00:00'::interval), 'MM/DD/YYYY hh12:mi AM'::text) AS "Discharge Date and Time",
    to_char((cs.discharged_datetime + '08:00:00'::interval), 'MM/DD/YYYY'::text) AS "Discharge Date ",
    to_char((cs.discharged_datetime + '08:00:00'::interval), 'hh12:mi AM'::text) AS "Discharge Time",
    cs.discharge_disposition AS "Disposition",
    cs.discharge_condition AS "Condition",
    cs.discharge_diagnosis AS "Final Diagnosis"
   FROM ((pms.cases cs
     LEFT JOIN pms.patients pt ON ((pt.id = cs.patient)))
     LEFT JOIN hrm.employees emp ON ((emp.id = cs.attending_physician)))
  WHERE ((cs.registry_type)::text = 'IPD'::text)
  ORDER BY cs.admission_datetime;
COMMENT ON VIEW "pms"."inpatient_report_mamcyra" IS NULL;