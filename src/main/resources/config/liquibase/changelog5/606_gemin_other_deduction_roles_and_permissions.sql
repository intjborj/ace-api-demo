ALTER TABLE hrm.employee_other_deductions ADD PRIMARY KEY (employee, other_deduction);

INSERT INTO t_authority (name)
SELECT 'OTHER_DEDUCTION_ROLE'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'OTHER_DEDUCTION_ROLE'
  );


INSERT INTO t_permission (name, description)
    SELECT 'permission_to_view_other_deduction', 'Permission to View Other Deduction'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_view_other_deduction'
    );

INSERT INTO t_permission (name, description)
    SELECT 'permission_to_edit_other_deduction', 'Permission to Edit Other Deduction'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_edit_other_deduction'
    );

INSERT INTO t_permission (name, description)
    SELECT 'permission_to_delete_other_deduction', 'Permission to Delete Other Deduction'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_delete_other_deduction'
    );

INSERT INTO t_permission (name, description)
    SELECT 'permission_to_create_other_deduction', 'Permission to Create Other Deduction'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_create_other_deduction'
    );

INSERT INTO t_permission (name, description)
    SELECT 'permission_to_update_other_deduction_employee_list', 'Permission to Update Other Deduction Employee List'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_update_other_deduction_employee_list'
    );


