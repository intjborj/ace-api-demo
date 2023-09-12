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

INSERT INTO public.t_group_policy_permission (group_policy_id, permission_name) VALUES ('24670153-dc13-4479-bf37-721508052a25', 'edit_patient');
INSERT INTO public.t_group_policy_permission (group_policy_id, permission_name) VALUES ('24670153-dc13-4479-bf37-721508052a25', 'add_employee');
INSERT INTO public.t_group_policy_permission (group_policy_id, permission_name) VALUES ('24670153-dc13-4479-bf37-721508052a25', 'edit_employee');
INSERT INTO public.t_group_policy_permission (group_policy_id, permission_name) VALUES ('24670153-dc13-4479-bf37-721508052a25', 'delete_employee');
INSERT INTO public.t_group_policy_permission (group_policy_id, permission_name) VALUES ('24670153-dc13-4479-bf37-721508052a25', 'delete_patient');
INSERT INTO public.t_group_policy_permission (group_policy_id, permission_name) VALUES ('24670153-dc13-4479-bf37-721508052a25', 'add_patient');

INSERT INTO t_permission (name, description)
VALUES ('allow_save_update_patient', 'Permission to Save/Update the Patient Information ');

INSERT INTO t_permission (name, description)
VALUES ('allow_save_case_admission', 'Permission to Save/Update the Patient Case Admission');

INSERT INTO t_permission (name, description)
VALUES ('allow_save_case_history', 'Permission to Save/Update the Patient Case History');

INSERT INTO t_permission (name, description)
VALUES ('allow_save_case_discharge', 'Permission to Save/Update the Patient Case Discharge');

INSERT INTO t_permission (name, description)
VALUES ('allow_add_nurse_notes', 'Permission to Add Nurse Notes');

INSERT INTO t_permission (name, description)
VALUES ('allow_add_vitals', 'Permission to Add Vital Signs data');

INSERT INTO t_permission (name, description)
VALUES ('allow_add_intake', 'Permission to Add Intake data');

INSERT INTO t_permission (name, description)
VALUES ('allow_add_output', 'Permission to Add Output data');

INSERT INTO t_permission (name, description)
VALUES ('allow_add_medication', 'Permission to add medication to medication sheet');

INSERT INTO t_permission (name, description)
VALUES ('allow_quick_administer', 'Permission to Quick Administer medication');

INSERT INTO t_permission (name, description)
VALUES ('allow_request_medication', 'Permission to Request medication');

INSERT INTO t_permission (name, description)
VALUES ('allow_medication_actions', 'Permission to select medication actions');

INSERT INTO t_permission (name, description)
VALUES ('allow_remove_medication_administration', 'Permission to remove administration logs');

INSERT INTO t_permission (name, description)
VALUES ('allow_trigger_maygohomoe', 'Permission to tag patient as May Go Home ');

INSERT INTO t_permission (name, description)
VALUES ('allow_trigger_discharge', 'Permission to discharge patient');


-- Ancillary Permissions

INSERT INTO public.t_permission(name, description)
    SELECT 'add_result', 'Permission to Add Result'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='add_result'
    );
INSERT INTO public.t_permission(name, description)
SELECT 'process_order', 'Permission to Process OrderSlip'
WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='process_order'
    );
INSERT INTO public.t_permission(name, description)
SELECT 'cancel_order', 'Permission to Cancel Order'
WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='cancel_order'
    );
