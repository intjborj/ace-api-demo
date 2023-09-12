INSERT INTO t_permission (name, description)
SELECT 'allow_view_er_patients', 'Permission to view ER Patients'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_view_er_patients'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_view_out_patients', 'Permission to view Out Patients'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_view_out_patients'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_view_in_patients', 'Permission to view In Patients'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_view_in_patients'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_view_my_patients', 'Permission to view own Patients'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_view_my_patients'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_view_all_patients', 'Permission to view all Patients'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_view_all_patients'
  );