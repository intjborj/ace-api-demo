INSERT INTO public.t_permission(name, description)
SELECT 'void_check_encashment', 'Permission to void check encashment'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'void_check_encashment'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'add_check_encashment', 'Permission to void check encashment'
WHERE NOT EXISTS(
  SELECT 1 FROM public.t_permission WHERE name = 'add_check_encashment'
);