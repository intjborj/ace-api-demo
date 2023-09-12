INSERT INTO t_permission (name,description)
SELECT 'allow_to_print_folio_entries','Permission to print folio entries'
WHERE NOT EXISTS(
 SELECT 1 FROM public.t_permission WHERE name = 'allow_to_print_folio_entries'
);
INSERT INTO t_permission (name,description)
SELECT 'allow_to_print_soa','Permission to print soa'
WHERE NOT EXISTS(
 SELECT 1 FROM public.t_permission WHERE name = 'allow_to_print_soa'
);
INSERT INTO t_permission (name,description)
SELECT 'allow_to_group_soa_misc', 'Permission to group soa misc'
WHERE NOT EXISTS(
 SELECT 1 FROM public.t_permission WHERE name = 'allow_to_group_soa_misc'
);
INSERT INTO t_permission (name,description)
SELECT 'allow_to_add_package', 'Permission to add package'
WHERE NOT EXISTS(
 SELECT 1 FROM public.t_permission WHERE name = 'allow_to_add_package'
);
INSERT INTO t_permission (name,description)
SELECT 'allow_to_finalized_folio', 'Permission to finalized folio'
WHERE NOT EXISTS(
 SELECT 1 FROM public.t_permission WHERE name = 'allow_to_finalized_folio'
);
INSERT INTO t_permission (name,description)
SELECT 'allow_to_progress_payment', 'Permission to progress payment'
WHERE NOT EXISTS(
 SELECT 1 FROM public.t_permission WHERE name = 'allow_to_progress_payment'
);
INSERT INTO t_permission (name,description)
SELECT 'allow_to_lock_unlock_case', 'Permission to lock or unlock case'
WHERE NOT EXISTS(
 SELECT 1 FROM public.t_permission WHERE name = 'allow_to_lock_unlock_case'
);
