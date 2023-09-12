
INSERT INTO public.t_permission(name, description)
SELECT 'manage_child_department_work_schedule', 'Permission to Manage Child Department Work Schedule'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_child_department_work_schedule'
  );
