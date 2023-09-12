DROP VIEW pms.patient_case_view;

ALTER TABLE pms.patients
    ALTER COLUMN address TYPE text,
    ALTER COLUMN state_province TYPE varchar(300),
    ALTER COLUMN city_municipality TYPE varchar(300),
    ALTER COLUMN country TYPE varchar(300),
    ALTER COLUMN barangay TYPE varchar(300),
    ALTER COLUMN father TYPE varchar(300),
    ALTER COLUMN mother TYPE varchar(300),
    ALTER COLUMN father_occupation TYPE varchar(300),
    ALTER COLUMN mother_occupation TYPE varchar(300),
    ALTER COLUMN email_address TYPE varchar(300),
    ALTER COLUMN nationality TYPE varchar(300),
    ALTER COLUMN patient_no TYPE varchar(50);

CREATE OR REPLACE VIEW pms.patient_case_view
    AS
     SELECT DISTINCT p.id,
    p.patient_no,
    p.last_name AS patient_lastname,
    p.first_name AS patient_firstname,
    p.middle_name AS patient_middlename,
    p.name_suffix AS patient_suffix,
    c.id AS caseid,
    c.status,
    c.discharge_condition,
    c.discharge_disposition,
    e.first_name AS managing_firstname,
    e.last_name AS managing_lastname,
    e.id AS primaryphysician,
    m."position",
    c.admission_datetime,
    c.transferred_in,
    c.transferred_out,
    c.registry_type,
    c.may_go_home_datetime,
    c.room,
    c.service_type,
    c.transfer_hci,
    c.discharged_datetime,
    c.originating_hci,
    array_to_string(array_agg(e2.id ORDER BY e2.last_name), ','::text) AS managing_staffs,
	array_to_string(array_agg(e3.id ORDER BY e3.last_name), ','::text) AS comanaging_physicians
   FROM pms.patients p
     LEFT JOIN ( SELECT cases.id,
            cases.case_no,
            cases.status,
            cases.service_type,
            cases.accommodation_type,
            cases.registry_type,
            cases.entry_datetime,
            cases.admission_datetime,
            cases.discharged_datetime,
            cases.may_go_home_datetime,
            cases.admitting_diagnosis,
            cases.discharge_diagnosis,
            cases.pre_op_diagnosis,
            cases.post_op_diagnosis,
            cases.history_present_illness,
            cases.past_medical_history,
            cases.surgical_procedure,
            cases.informant,
            cases.informant_relation,
            cases.informant_address,
            cases.patient,
            cases.created_by,
            cases.created_date,
            cases.last_modified_by,
            cases.last_modified_date,
            cases.room,
            cases.admission_date,
            cases.attending_physician,
            cases.admitting_physician,
            cases.chief_complaint,
            cases.occupation,
            cases.company_name,
            cases.company_address,
            cases.company_contact,
            cases.emergency_contact_name,
            cases.emergency_contact_address,
            cases.emergency_contact_relation,
            cases.emergency_contact,
            cases.guarantor_name,
            cases.guarantor_address,
            cases.guarantor_relation,
            cases.guarantor_contact,
            cases.informant_contact,
            cases.history_input_datetime,
            cases.triage,
            cases.height,
            cases.weight,
            cases.initial_bp,
            cases.initial_temperature,
            cases.initial_pulse,
            cases.initial_resp,
            cases.initial_o2sat,
            cases.followup_datetime,
            cases.reason_for_transfer_out,
            cases.home_medication,
            cases.special_instructions,
            cases.lacerated_wound,
            cases.head_injury,
            cases.pertinent_past_medical_history,
            cases.transferred_in,
            cases.reason_for_transfer_in,
            cases.originating_hci,
            cases.department,
            cases.icd_diagnosis,
            cases.rvs_diagnosis,
            cases.transferred_out,
            cases.transfer_hci,
            cases.how_taken_to_room,
            cases.previous_admission,
            cases.physical_exam_list,
            cases.pertinent_symptoms_list,
            cases.primary_dx,
            cases.secondary_dx,
            cases.take_home_medications,
            cases.room_in,
            cases.additional_rooms,
            cases.price_tier_detail,
            cases.is_infacility_delivery,
            cases.delivery_type,
            cases.is_antenatal,
            cases.is_postnatal,
            cases.discharge_condition,
            cases.time_of_death,
            cases.is_dead_on_arrival,
            cases.death_type,
            cases.operation_code,
            cases.credit_limit,
            cases.diet,
            cases.rcid,
            cases.service_code,
            cases.doh_surgical_diagnosis,
            cases.doh_icd_diagnosis,
            cases.discharge_disposition,
            cases.hmo_company,
            cases.time_of_birth,
            cases.course_in_the_ward
           FROM pms.cases
          WHERE cases.status::text = 'ACTIVE'::text) c ON c.patient = p.id
     LEFT JOIN ( SELECT m_1.id,
            m_1.employee,
            m_1."case",
            m_1.created_by,
            m_1.created_date,
            m_1.last_modified_by,
            m_1.last_modified_date,
            m_1.deleted,
            m_1."position"
           FROM pms.managing_physicians m_1
          WHERE m_1.is_main = true) m ON m."case" = c.id
     LEFT JOIN hrm.employees e ON m.employee = e.id
     LEFT JOIN ( SELECT m_2.id,
            m_2.employee,
            m_2."case",
            m_2.created_by,
            m_2.created_date,
            m_2.last_modified_by,
            m_2.last_modified_date,
            m_2.deleted,
            m_2."position"
           FROM pms.managing_physicians m_2
          WHERE m_2."position"::text = 'STAFF'::text) m2 ON m2."case" = c.id
     LEFT JOIN hrm.employees e2 ON m2.employee = e2.id
     LEFT JOIN ( SELECT m_3.id,
            m_3.employee,
            m_3."case",
            m_3.created_by,
            m_3.created_date,
            m_3.last_modified_by,
            m_3.last_modified_date,
            m_3.deleted,
            m_3."position"
           FROM pms.managing_physicians m_3
          WHERE m_3.is_main = false OR m_3.is_main IS NULL) m3 ON m3."case" = c.id
     LEFT JOIN hrm.employees e3 ON m.employee = e3.id
  GROUP BY p.id, c.id, c.status, c.discharge_condition, c.discharge_disposition, e.first_name, e.last_name, e.id, m."position", c.admission_datetime, c.transferred_in, c.transferred_out, c.registry_type, c.may_go_home_datetime, c.room, c.service_type, c.transfer_hci, c.discharged_datetime, c.originating_hci
  ORDER BY p.last_name;