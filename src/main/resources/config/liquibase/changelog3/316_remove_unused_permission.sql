DELETE FROM public.t_user_permission tup where tup.permission_name in ('accounting_configuration_update', 'accounting_configuration_add');
DELETE FROM public.t_permission tp where tp.name in ('accounting_configuration_update','accounting_configuration_add');
