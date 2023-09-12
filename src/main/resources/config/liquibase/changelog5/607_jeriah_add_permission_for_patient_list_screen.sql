    INSERT INTO t_permission (name, description)
    SELECT 'patient_list_screen', 'Permission to Patient List Screen'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'patient_list_screen'
    );


