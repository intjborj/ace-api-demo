INSERT INTO t_permission (name, description)
    SELECT 'allow_ignore_hide_doctors_order', 'Permission to ignore or hide doctors orders'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'allow_ignore_hide_doctors_order'
    );

INSERT INTO t_permission (name, description)
    SELECT 'allow_ignore_hide_doctors_order_item', 'Permission to ignore or hide doctors order'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'allow_ignore_hide_doctors_order_item'
    );