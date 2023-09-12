

INSERT INTO public.t_permission(name, description)
SELECT 'manage_raw_logs', 'Permission to Manage Raw Logs'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_raw_logs'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'manage_work_schedule', 'Permission to Manage Work Schedule'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_raw_logs'
  );
