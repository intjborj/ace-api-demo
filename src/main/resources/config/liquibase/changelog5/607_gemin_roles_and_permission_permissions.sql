

INSERT INTO t_permission (name, description)
  SELECT 'permission_to_assign_roles_and_permissions', 'Permission to Assign Roles and Permissions'
  WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_assign_roles_and_permissions'
  );
