 INSERT INTO t_permission (name, description)
    SELECT 'add_dietitian_note', 'Permission to Add Dietitian note'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'add_dietitian_note'
    );

 INSERT INTO t_permission (name, description)
    SELECT 'edit_dietitian_note', 'Permission to edit Dietitian note'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'edit_dietitian_note'
    );

 INSERT INTO t_permission (name, description)
     SELECT 'delete_dietitian_note', 'Permission to delete Dietitian note'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'delete_dietitian_note'
    );

 INSERT INTO t_permission (name, description)
    SELECT 'edit_date_time_dietitian_note', 'Permission to Edit Date and Time Dietitian note'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'edit_date_time_dietitian_note'
    );