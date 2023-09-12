INSERT INTO t_authority (name)
SELECT 'ALLOWANCE_ROLE'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'ALLOWANCE_ROLE'
  );


INSERT INTO t_permission (name, description)
  SELECT 'permission_to_edit_allowance', 'Permission to Edit Allowance'
  WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_edit_allowance'
  );


INSERT INTO t_permission (name, description)
  SELECT 'permission_to_delete_allowance', 'Permission to Delete Allowance'
  WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_delete_allowance'
  );

INSERT INTO t_permission (name, description)
    SELECT 'permission_to_create_allowance', 'Permission to Create Allowance'
    WHERE NOT EXISTS(
      SELECT 1 FROM public.t_permission WHERE name = 'permission_to_create_allowance'
    );


