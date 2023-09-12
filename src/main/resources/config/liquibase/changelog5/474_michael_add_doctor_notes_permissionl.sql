

INSERT INTO public.t_permission(name, description)
SELECT 'allow_user_to_add_doctor_notes', 'Permission to Add Doctor Notes'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_user_to_add_doctor_notes'
  );


INSERT INTO public.t_permission(name, description)
SELECT 'allow_user_to_edit_doctor_notes', 'Permission to Edit Doctor Notes'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_user_to_edit_doctor_notes'
  );



INSERT INTO public.t_permission(name, description)
SELECT 'allow_user_to_delete_doctor_notes', 'Permission to Delete Doctor Notes'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_user_to_delete_doctor_notes'
  );
