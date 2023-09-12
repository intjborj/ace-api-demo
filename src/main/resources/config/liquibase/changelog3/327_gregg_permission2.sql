INSERT INTO t_permission (name, description)
SELECT 'allow_patient_census_view', 'Permission to View Patient Dashboard Census'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_patient_census_view'
  );