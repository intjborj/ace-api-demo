INSERT INTO t_permission (name, description)
SELECT 'allow_discontinue_doctors_orders', 'Permission to Discontinue Doctors Order'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_discontinue_doctors_orders'
  );