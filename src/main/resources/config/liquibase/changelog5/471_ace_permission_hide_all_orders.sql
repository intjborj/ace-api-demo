INSERT INTO t_permission (name, description)
    SELECT 'allow_all_ignore_hide_doctors_order', 'Permission to ignore or hide all doctors orders'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'allow_all_ignore_hide_doctors_order'
    );