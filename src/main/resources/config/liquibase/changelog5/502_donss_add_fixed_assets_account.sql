INSERT INTO accounting.chart_of_accounts (id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted, account_type, fs_type, normal_side, is_contra)
  SELECT '2eba2af1-e55e-45d1-baf4-10c4fb60b0eb'::uuid, '600602', 'FIXED ASSET', NULL, NULL, NULL, NULL, NULL, now(), NULL, now(), NULL, 'ASSET', 'INCOME', 'DEBIT', false
  WHERE NOT EXISTS(
    SELECT 1 FROM accounting.chart_of_accounts WHERE id = '2eba2af1-e55e-45d1-baf4-10c4fb60b0eb'
  );