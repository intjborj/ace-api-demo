INSERT INTO t_permission (name, description)
SELECT 'can_print_wristband', 'Permission to Print Wristband'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'can_print_wristband'
  );