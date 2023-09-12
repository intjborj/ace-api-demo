INSERT INTO t_authority (name)
SELECT 'PAYROLL_MANAGER'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'PAYROLL_MANAGER'
  );

INSERT INTO t_authority (name)
SELECT 'TIMEKEEPING_USER'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'TIMEKEEPING_USER'
  );


INSERT INTO t_permission (name, description)
    SELECT 'create_new_payroll', 'Permission to Create New Payroll'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'create_new_payroll'
    );

INSERT INTO t_permission (name, description)
    SELECT 'view_payroll', 'Permission to View Payroll'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'view_payroll'
    );

INSERT INTO t_permission (name, description)
    SELECT 'edit_payroll_details', 'Permission to Edit Payroll Details'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'edit_payroll_details'
    );

INSERT INTO t_permission (name, description)
    SELECT 'delete_payroll', 'Permission to Delete Payroll'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'delete_payroll'
    );

INSERT INTO t_permission (name, description)
    SELECT 'start_payroll', 'Permission to Start Payroll'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'start_payroll'
    );

INSERT INTO t_permission (name, description)
    SELECT 'cancel_payroll', 'Permission to Cancel Payroll'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'cancel_payroll'
    );

INSERT INTO t_permission (name, description)
    SELECT 'add_remove_payroll_employees', 'Permission to Add/Remove Payroll Employees'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'add_remove_payroll_employees'
    );

INSERT INTO t_permission (name, description)
    SELECT 'recalculate_all_employee_accumulated_logs', 'Permission to Recalculate All Employee Accumulated Logs'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'recalculate_all_employee_accumulated_logs'
    );

INSERT INTO t_permission (name, description)
    SELECT 'download_timekeeping', 'Permission to Download Timekeeping'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'download_timekeeping'
    );

INSERT INTO t_permission (name, description)
    SELECT 'recalculate_employee_accumulated_logs', 'Permission to Recalculate Employee Accumulated Logs'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'recalculate_employee_accumulated_logs'
    );

INSERT INTO t_permission (name, description)
    SELECT 'recalculate_employee_day_accumulated_logs', 'Permission to Recalculate Employee Day Accumulated Logs'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'recalculate_employee_day_accumulated_logs'
    );

INSERT INTO t_permission (name, description)
    SELECT 'reallocate_employee_accumulated_logs', 'Permission to Reallocate Employee Accumulated Logs'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'reallocate_employee_accumulated_logs'
    );

INSERT INTO t_permission (name, description)
    SELECT 'set_timekeeping_employee_as_finalized', 'Permission to SET Timekeeping Employee as FINALIZED'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'set_timekeeping_employee_as_finalized'
    );

INSERT INTO t_permission (name, description)
    SELECT 'permission_to_set_timekeeping_employee_as_draft', 'Permission to SET Timekeeping Employee as DRAFT'
    WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_set_timekeeping_employee_as_draft'
    );