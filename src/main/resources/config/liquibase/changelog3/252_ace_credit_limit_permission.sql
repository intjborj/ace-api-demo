INSERT INTO public.t_permission(name, description)
	SELECT 'allow_update_credit_limit', 'Permission to update credit limit'
	WHERE NOT EXISTS (
		SELECT 1 FROM public.t_permission WHERE name='allow_update_credit_limit'
	);