INSERT INTO t_authority (name)
SELECT 'EMPLOYEE_ALLOWANCE_ROLE'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'EMPLOYEE_ALLOWANCE_ROLE'
  );


INSERT INTO t_permission (name, description)
    SELECT 'permission_to_assign_allowance_to_employees', 'Permission to Assign Allowance to Employees'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_assign_allowance_to_employees'
    );


