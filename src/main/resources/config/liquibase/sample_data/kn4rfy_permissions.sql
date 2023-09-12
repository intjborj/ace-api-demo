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
