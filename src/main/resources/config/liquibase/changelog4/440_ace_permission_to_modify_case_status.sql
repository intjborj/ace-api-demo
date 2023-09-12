DELETE FROM t_permission WHERE name = 'permission_to_discharge_patient';

INSERT INTO public.t_permission(name, description)
SELECT 'permission_to_modify_case_status', 'Permission to modify case status'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_modify_case_status'
  );