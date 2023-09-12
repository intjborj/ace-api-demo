INSERT INTO t_authority (name)
SELECT 'ALLOWANCE_TEMPLATE_ROLE'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'ALLOWANCE_TEMPLATE_ROLE'
  );


INSERT INTO t_permission (name, description)
  SELECT 'permission_to_edit_allowance_template', 'Permission to Edit Allowance Template'
  WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_edit_allowance_template'
  );


INSERT INTO t_permission (name, description)
  SELECT 'permission_to_delete_allowance_template', 'Permission to Delete Allowance Template'
  WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_delete_allowance_template'
  );

INSERT INTO t_permission (name, description)
    SELECT 'permission_to_create_allowance_template', 'Permission to Create Allowance Template'
    WHERE NOT EXISTS(
      SELECT 1 FROM public.t_permission WHERE name = 'permission_to_create_allowance_template'
    );

INSERT INTO t_permission (name, description)
    SELECT 'permission_to_edit_allowance_template_status', 'Permission to Edit Allowance Template Status'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_edit_allowance_template_status'
    );

INSERT INTO t_permission (name, description)
    SELECT 'permission_to_edit_allowance_template_item_status', 'Permission to Edit Allowance Template Item Status'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_edit_allowance_template_item_status'
    );

INSERT INTO t_permission (name, description)
    SELECT 'permission_to_delete_allowance_template_item', 'Permission to Delete Allowance Template Item'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_delete_allowance_template_item'
    );



