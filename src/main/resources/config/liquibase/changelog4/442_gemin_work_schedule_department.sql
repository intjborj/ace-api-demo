
INSERT INTO public.t_permission(name, description)
SELECT 'manage_all_department_schedule', 'Permission to Manage All Department Work Schedule'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_all_department_schedule'
  );
