INSERT INTO t_permission (name, description)
SELECT 'allow_download_inpatient_report', 'Permission to Download Inpatient Report'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_download_inpatient_report'
  );