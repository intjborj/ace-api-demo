INSERT INTO public.t_permission(name, description)
SELECT 'lock_unlock_case', 'Permission to Lock or Unlock Case'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'lock_unlock_case'
  );