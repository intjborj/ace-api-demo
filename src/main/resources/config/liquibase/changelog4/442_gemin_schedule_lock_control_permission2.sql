INSERT INTO public.t_permission(name, description)
SELECT 'manage_schedule_lock', 'Permission to Manage Schedule Locks'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_schedule_lock'
  );
