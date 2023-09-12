ALTER TABLE "pms"."cases"
    ADD COLUMN "for_admission" timestamp;

INSERT INTO public.t_permission(name, description)
    SELECT 'allow_update_nurse_notes', 'Permission to update nurse notes'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_update_nurse_notes'
    );

INSERT INTO public.t_permission(name, description)
    SELECT 'allow_delete_nurse_notes', 'Permission to delete nurse notes'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_delete_nurse_notes'
    );

INSERT INTO public.t_permission(name, description)
    SELECT 'allow_update_vitals', 'Permission to update vital signs'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_update_vitals'
    );

INSERT INTO public.t_permission(name, description)
    SELECT 'allow_delete_vitals', 'Permission to delete vital signs'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_delete_vitals'
    );

INSERT INTO public.t_permission(name, description)
    SELECT 'allow_update_intake', 'Permission to update intakes'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_update_intake'
    );

INSERT INTO public.t_permission(name, description)
    SELECT 'allow_delete_intake', 'Permission to delete intakes'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_delete_intake'
    );

INSERT INTO public.t_permission(name, description)
    SELECT 'allow_update_output', 'Permission to update outputs'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_update_output'
    );

INSERT INTO public.t_permission(name, description)
    SELECT 'allow_delete_output', 'Permission to delete outputs'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='allow_delete_output'
    );