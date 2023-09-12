INSERT INTO public.t_permission(name, description)
    SELECT 'allow_update_service', 'Permission to update Service'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_update_service'
    );

INSERT INTO public.t_permission(name, description)
    SELECT 'allow_delete_service', 'Permission to delete Service'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_delete_service'
    );

INSERT INTO public.t_permission(name, description)
    SELECT 'allow_add_services', 'Permission to add Service'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_add_services'
    );
