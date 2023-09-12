INSERT INTO public.t_permission(name, description)
    SELECT 'add_result', 'Permission to Add Result'
    WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='add_result'
    );
INSERT INTO public.t_permission(name, description)
SELECT 'process_order', 'Permission to Process OrderSlip'
WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='process_order'
    );
INSERT INTO public.t_permission(name, description)
SELECT 'cancel_order', 'Permission to Cancel Order'
WHERE NOT EXISTS (
        SELECT 1 FROM public.t_permission WHERE name='cancel_order'
    );
