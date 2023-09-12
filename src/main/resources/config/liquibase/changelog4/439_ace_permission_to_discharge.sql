INSERT INTO public.t_permission(name, description)
SELECT 'permission_to_discharge_patient', 'Permission to Discharge Patient'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_discharge_patient'
  );