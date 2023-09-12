INSERT INTO public.t_permission(name, description)
SELECT 'manage_locked_work_schedule', 'Permission to Manage Work Schedule on Locked Dates'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'manage_locked_work_schedule'
  );

insert into t_authority(name) values ('SCHEDULE_LOCK');