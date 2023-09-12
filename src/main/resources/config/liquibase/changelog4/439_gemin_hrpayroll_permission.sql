

INSERT INTO public.t_permission(name, description)
SELECT 'manage_employee', 'Permission to Manage Employee'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_employee'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'sync_biometric_logs', 'Permission to Sync Employee Biometric Logs'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'sync_biometric_logs'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'ignore_unignore_attendance_logs', 'Permission to Ignore/Unignore Attendance Logs'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'ignore_unignore_attendance_logs'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'view_detailed_accumulated_log', 'Permission to View Detailed Accumulated Log'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'view_detailed_accumulated_log'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'view_detailed_perf_summary', 'Permission to View Detailed Performance Summary'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'view_detailed_perf_summary'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'manage_work_schedule', 'Permission to Manage Work Schedule'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_work_schedule'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'manage_ot_work_schedule', 'Permission to Manage Overtime Work Schedule'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_ot_work_schedule'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'manage_rest_day_work_schedule', 'Permission to Manage Rest Day Work Schedule'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_rest_day_work_schedule'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'manage_oic_work_schedule', 'Permission to Manage OIC Work Schedule'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_oic_work_schedule'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'add_custom_work_schedule', 'Permission to Add Custom Work Schedule'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'add_custom_work_schedule'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'manage_holiday_event', 'Permission to Manage Holiday/Event Schedule'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_holiday_event'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'manage_dept_sched_config', 'Permission to Manage Department Schedule Configuration'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_dept_sched_config'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'copy_dept_sched_config', 'Permission to Copy Department Schedule Configuration'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'copy_dept_sched_config'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'clear_department_schedule_config', 'Permission to Clear Department Schedule Configuration'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'clear_department_schedule_config'
  );

INSERT INTO public.t_permission(name, description)
SELECT 'manage_biometric_device', 'Permission to Manage Biometric Device'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_biometric_device'
  );

insert into t_authority(name) values ('BIOMETRIC_MANAGEMENT');