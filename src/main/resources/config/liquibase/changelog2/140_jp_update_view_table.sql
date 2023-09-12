CREATE OR REPLACE VIEW pms.patient_case_view
    WITH (security_barrier=false)
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
	c.transfer_hci
   FROM pms.patients p
     JOIN ( SELECT cases.id,
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
          ORDER BY cases.admission_date) c ON c.patient = p.id
     LEFT JOIN pms.managing_physicians m ON m."case" = c.id AND m."position"::text = 'ATTENDING_PHYSICIAN'::text
     LEFT JOIN hrm.employees e ON m.employee = e.id
  ORDER BY p.last_name;
