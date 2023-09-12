INSERT INTO t_permission (name, description)
  SELECT 'allow_modify_all_discharge_clearance', 'Permission to Clear All Discharge Clearances'
  WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_modify_all_discharge_clearance'
  );