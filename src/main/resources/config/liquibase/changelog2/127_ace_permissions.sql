INSERT INTO t_permission (name, description)
SELECT 'allow_view_doctors_orders_status', 'Permission to View Doctors Orders status'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_view_doctors_orders_status'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_update_doctors_orders', 'Permission to Update Doctors Orders status'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_update_doctors_orders'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_add_doctors_orders', 'Permission to Add Doctors Orders'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_add_doctors_orders'
  );