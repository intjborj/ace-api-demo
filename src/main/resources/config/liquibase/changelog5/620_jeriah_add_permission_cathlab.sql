 INSERT INTO t_permission (name, description)
    SELECT 'add_cathlab_nurse_note', 'Permission to Add Cathlab nurse note'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'add_cathlab_nurse_note'
    );

 INSERT INTO t_permission (name, description)
    SELECT 'edit_cathlab_nurse_note', 'Permission to edit Cathlab nurse note'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'edit_cathlab_nurse_note'
    );

 INSERT INTO t_permission (name, description)
     SELECT 'delete_cathlab_nurse_note', 'Permission to delete Cathlab nurse note'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'delete_cathlab_nurse_note'
    );

 INSERT INTO t_permission (name, description)
    SELECT 'edit_date_time_cathlab_nurse_note', 'Permission to Edit Date and Time Cathlab nurse note'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'edit_date_time_cathlab_nurse_note'
    );


