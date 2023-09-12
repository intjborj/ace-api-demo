 INSERT INTO t_permission (name, description)
    SELECT 'attending_physician_list', 'Permission to View Attending Physician Patient List'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'attending_physician_list'
    );

 INSERT INTO t_permission (name, description)
    SELECT 'department_patient_list', 'Permission to View Department Patient List'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'department_patient_list'
    );

 INSERT INTO t_permission (name, description)
     SELECT 'filter_patient_list', 'Permission to View Filter Patient List'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'filter_patient_list'
    );

 INSERT INTO t_permission (name, description)
    SELECT 'patient_report', 'Permission to to View Patient Reports'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'patient_report'
    );


