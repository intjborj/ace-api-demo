CREATE VIEW "public"."inpatient_all" AS select
	pt.patient_no,cs.case_no,
	cs.entry_datetime+ interval '8 hour' as entry_datetime,
	cs.admission_datetime+ interval '8 hour' as admission_datetime,
	cs.admission_datetime::TIMESTAMP::DATE as date_admission,
	pt.last_name,
	pt.first_name,
	pt.middle_name,
	pt.dob,
	age(cs.admission_datetime::TIMESTAMP::DATE, pt.dob) as age,
	pt.gender,
	pt.address,
	pt.barangay,
	pt.city_municipality,
	ct.district,
	pt.state_province,
	pt.country,
	pt.zip_code,
	pt.civil_status,
	cs.status,
	cs.discharged_datetime + interval '8 hour' as discharged_datetime,
	cs.discharge_diagnosis,
	cs.discharge_condition,
	emp.first_name || ' ' || emp.last_name as attending,
	cs.doh_icd_diagnosis
from pms.cases cs
left join pms.patients pt on pt.id = cs.patient
left join "hrm".employees emp on emp.id = cs.attending_physician
left join public.cities ct on TRIM(BOTH ' ' from UPPER(pt.city_municipality)) = TRIM(BOTH ' ' from UPPER(ct.name))
where cs.registry_type = 'IPD'
order by cs.admission_datetime;