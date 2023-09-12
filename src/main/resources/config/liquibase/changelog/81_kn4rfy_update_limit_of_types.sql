alter table pms.nurse_notes
  alter column entry_datetime type timestamp(6),
  alter column focus type varchar(255),
  alter column data type varchar(255),
  alter column action type varchar(255),
  alter column response type varchar(255),
  alter column note type varchar(255),
  add column employee uuid;

alter table pms.intakes
  alter column entry_datetime type timestamp(6),
  alter column po_intake type varchar(50),
  alter column tube_intake type varchar(50),
  alter column ivf_intake type varchar(50),
  alter column blood_intake type varchar(50),
  alter column tpn_intake type varchar(50),
  alter column pb_intake type varchar(50),
  alter column medication_intake type varchar(50),
  add column employee uuid;

alter table pms.outputs
  alter column entry_datetime type timestamp(6),
  alter column voided_output type varchar(50),
  alter column catheter_output type varchar(50),
  alter column ng_output type varchar(50),
  alter column insensible_loss_output type varchar(50),
  alter column emesis_output type varchar(50),
  alter column stool_output type varchar(50),
  add column employee uuid;

alter table pms.vital_signs
  alter column entry_datetime type timestamp(6),
  alter column systolic type varchar(10),
  alter column diastolic type varchar(10),
  alter column temperature type varchar(10),
  alter column pulse_rate type varchar(10),
  alter column respiratory_rate type varchar(10),
  alter column oxygen_saturation type varchar(10),
  add column employee uuid;

alter table pms.medications
  alter column entry_datetime type timestamp(6),
  alter column frequency type varchar(50),
  alter column dose type varchar(50),
  alter column route type varchar(50),
  alter column remarks type varchar(50),
  alter column type type varchar(50);

alter table pms.administrations
  alter column entry_datetime type timestamp(6),
  alter column action type varchar(50),
  alter column dose type varchar(50),
  alter column remarks type varchar(50);

alter table pms.transfers
  alter column registry_type type varchar(50),
  alter column status type varchar(50);

alter table pms.cases
  alter column entry_datetime type timestamp(6),
  alter column admission_datetime type timestamp(6),
  alter column discharged_datetime type timestamp(6),
  alter column may_go_home_datetime type timestamp(6),
  alter column admission_date type timestamp(6),
  alter column case_no type varchar(10),
  alter column status type varchar(50),
  alter column service_type type varchar(50),
  alter column accommodation_type type varchar(50),
  alter column registry_type type varchar(50),
  alter column admitting_diagnosis type text,
  alter column discharge_diagnosis type text,
  alter column pre_op_diagnosis type text,
  alter column post_op_diagnosis type text,
  alter column history_present_illness type text,
  alter column past_medical_history type text,
  alter column surgical_procedure type text,
  alter column informant type varchar(50),
  alter column informant_relation type varchar(50),
  alter column informant_address type varchar(50),
  alter column chief_complaint type varchar(255),
  alter column occupation type varchar(50),
  alter column company_name type varchar(50),
  alter column company_address type varchar(50),
  alter column company_contact type varchar(50),
  alter column emergency_contact_name type varchar(50),
  alter column emergency_contact_address type varchar(50),
  alter column emergency_contact_relation type varchar(50),
  alter column emergency_contact type varchar(50),
  alter column guarantor_name type varchar(50),
  alter column guarantor_address type varchar(50),
  alter column guarantor_relation type varchar(50),
  alter column guarantor_contact type varchar(50),
  alter column informant_contact type varchar(50),
  alter column triage type varchar(50),
  alter column height type varchar(10),
  alter column weight type varchar(10),
  alter column initial_bp type varchar(10),
  alter column initial_temperature type varchar(10),
  alter column initial_pulse type varchar(10),
  alter column initial_resp type varchar(10),
  alter column initial_o2sat type varchar(10),
  alter column disposition type varchar(50),
  alter column reason_for_transfer_out type varchar(255),
  alter column lacerated_wound type varchar(255),
  alter column head_injury type varchar(255),
  alter column pertinent_past_medical_history type varchar(255),
  alter column reason_for_transfer_in type varchar(255),
  alter column originating_hci type varchar(50),
  alter column transfer_hci type varchar(50),
  alter column how_taken_to_room type varchar(50),
  alter column previous_admission type varchar(50);

alter table pms.patients
  alter column patient_no type varchar(10),
  alter column last_name type varchar(50),
  alter column first_name type varchar(50),
  alter column middle_name type varchar(50),
  alter column name_suffix type varchar(50),
  alter column address type varchar(50),
  alter column country type varchar(50),
  alter column state_province type varchar(50),
  alter column city_municipality type varchar(50),
  alter column barangay type varchar(50),
  alter column gender type varchar(50),
  alter column father type varchar(50),
  alter column mother type varchar(50),
  alter column father_occupation type varchar(50),
  alter column mother_occupation type varchar(50);