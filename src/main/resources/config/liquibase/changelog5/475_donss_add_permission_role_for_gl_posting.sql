INSERT INTO t_permission (name, description)
  SELECT 'permission_to_post_all_entries_to_gl', 'Permission to post all entries to gl'
  WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'permission_to_post_all_entries_to_gl'
  );