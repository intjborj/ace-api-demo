INSERT INTO accounting.chart_of_accounts(account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted, account_type, fs_type, normal_side, is_contra)
SELECT '600420', 'INTEREST EXPENSE', NULL, NULL, NULL, NULL, NULL, '2020-10-20 14:47:28.474', NULL, '2020-10-20 14:47:28.474', NULL, 'EXPENSE', 'INCOME', 'DEBIT', false
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.chart_of_accounts WHERE description = 'INTEREST EXPENSE'
  );

INSERT INTO accounting.integration(id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain")
SELECT uuid_generate_v1(), 'LOAN_JOURNAL_ENTRY', NULL, 'LOAN_ENTRY', 20, 'admin', '2022-05-15 14:23:46.203', 'admin', '2022-05-15 14:24:27.885', NULL, 'com.hisd3.hismk2.domain.accounting.Loan'
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration WHERE flag_value = 'LOAN_ENTRY'
  );

INSERT INTO accounting.integration(id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain")
SELECT uuid_generate_v1(), 'LOANS PAYMENT', NULL, 'LOANM_PAYMENT', 10, 'admin', '2022-05-15 13:52:28.362', 'admin', '2022-05-15 13:53:24.424', NULL, 'com.hisd3.hismk2.domain.accounting.LoanAmortization'
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration WHERE flag_value = 'LOANM_PAYMENT'
  );