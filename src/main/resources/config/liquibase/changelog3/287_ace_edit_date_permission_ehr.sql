INSERT INTO t_permission (name, description)
	SELECT 'allow_update_datetime_ehr', 'Permission to update datetime on EHR data'
	WHERE NOT EXISTS(
		SELECT 1 FROM public.t_permission WHERE name = 'allow_update_datetime_ehr'
	);