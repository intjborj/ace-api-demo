INSERT INTO t_permission (name, description)
SELECT 'allow_save_update_patient', 'Permission to Save/Update the Patient Information '
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_save_update_patient'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_save_case_admission', 'Permission to Save/Update the Patient Case Admission'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_save_case_admission'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_save_case_history', 'Permission to Save/Update the Patient Case History'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_save_case_history'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_save_case_discharge', 'Permission to Save/Update the Patient Case Discharge'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_save_case_discharge'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_add_nurse_notes', 'Permission to Add Nurse Notes'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_add_nurse_notes'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_add_vitals', 'Permission to Add Vital Signs data'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_add_vitals'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_add_intake', 'Permission to Add Intake data'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_add_intake'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_add_output', 'Permission to Add Output data'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_add_output'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_add_medication', 'Permission to add medication to medication sheet'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_add_medication'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_quick_administer', 'Permission to Quick Administer medication'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_quick_administer'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_request_medication', 'Permission to Request medication'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_request_medication'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_medication_actions', 'Permission to select medication actionsn'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_medication_actions'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_remove_medication_administration', 'Permission to remove administration log'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_remove_medication_administration'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_trigger_maygohomoe', 'Permission to tag patient as May Go Home'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_trigger_maygohomoe'
);

INSERT INTO t_permission (name, description)
SELECT 'allow_trigger_discharge', 'Permission to discharge patient'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_trigger_discharge'
);

INSERT INTO public.t_permission(name, description)
SELECT 'add_patient_case', 'Permission to Add Patient Case'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'add_patient_case'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'edit_patient_case', 'Permission to Edit Patient Case'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'edit_patient_case'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'view_patient_case', 'Permission to View Patient Case'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'view_patient_case'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'delete_patient_case', 'Permission to Delete Patient Case'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'delete_patient_case'
  );
