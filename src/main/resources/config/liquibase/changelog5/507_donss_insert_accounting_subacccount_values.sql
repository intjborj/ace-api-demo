INSERT INTO accounting.subaccount_setup (id, description, subaccount_code, subaccount_parent, subaccount_type, include_department, attr_beginning_balance, attr_credit_memo_adj, attr_accrual_of_income, attr_non_trade_cash_receipts, attr_include_posting_accrued_income_multiple_customer, attr_vatable, attr_inactive, attr_expense_account, attr_debit_memo_adjustment, attr_accrual_expense, source_domain, created_by, created_date, last_modified_by, last_modified_date, journal_placement, category, require_remarks, attached_value)
SELECT 'cc0d7f1d-2a83-4f4c-a796-957856e860f6'::uuid, 'PROPERTY, PLANT AND EQUIPMENT', '112', NULL, 'OTHERENTITIES', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 'admin', '2022-09-09 13:39:08.463', 'admin', '2022-09-09 13:39:08.463', NULL, NULL, NULL , NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.subaccount_setup WHERE id = 'cc0d7f1d-2a83-4f4c-a796-957856e860f6'
  );
INSERT INTO accounting.subaccount_setup (id, description, subaccount_code, subaccount_parent, subaccount_type, include_department, attr_beginning_balance, attr_credit_memo_adj, attr_accrual_of_income, attr_non_trade_cash_receipts, attr_include_posting_accrued_income_multiple_customer, attr_vatable, attr_inactive, attr_expense_account, attr_debit_memo_adjustment, attr_accrual_expense, source_domain, created_by, created_date, last_modified_by, last_modified_date, journal_placement, category, require_remarks, attached_value)
SELECT 'c61c7a9b-b6cf-4fd9-ad3c-292c8061dcf2'::uuid, 'FIXED ASSET PPE', 'PPE', 'cc0d7f1d-2a83-4f4c-a796-957856e860f6'::uuid, 'OTHERENTITIES', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'com.hisd3.hismk2.domain.inventory.AccountingCategory', 'admin', '2022-09-09 13:42:14.516', 'admin', '2022-09-09 13:42:14.516', NULL, NULL, NULL , NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.subaccount_setup WHERE id = 'c61c7a9b-b6cf-4fd9-ad3c-292c8061dcf2'
  );
INSERT INTO accounting.subaccount_setup (id, description, subaccount_code, subaccount_parent, subaccount_type, include_department, attr_beginning_balance, attr_credit_memo_adj, attr_accrual_of_income, attr_non_trade_cash_receipts, attr_include_posting_accrued_income_multiple_customer, attr_vatable, attr_inactive, attr_expense_account, attr_debit_memo_adjustment, attr_accrual_expense, source_domain, created_by, created_date, last_modified_by, last_modified_date, journal_placement, category, require_remarks, attached_value)
SELECT '756294b0-6f4e-4284-8ef6-a8df55036739'::uuid, 'ACCUMULATED DEPRECIATION', '114', NULL, 'OTHERENTITIES', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 'admin', '2022-09-09 14:33:50.049', 'admin', '2022-09-09 14:33:50.049', NULL, NULL, NULL , NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.subaccount_setup WHERE id = '756294b0-6f4e-4284-8ef6-a8df55036739'
  );
INSERT INTO accounting.subaccount_setup (id, description, subaccount_code, subaccount_parent, subaccount_type, include_department, attr_beginning_balance, attr_credit_memo_adj, attr_accrual_of_income, attr_non_trade_cash_receipts, attr_include_posting_accrued_income_multiple_customer, attr_vatable, attr_inactive, attr_expense_account, attr_debit_memo_adjustment, attr_accrual_expense, source_domain, created_by, created_date, last_modified_by, last_modified_date, journal_placement, category, require_remarks, attached_value)
SELECT '0869f239-296e-4ca9-a03f-9e392dca8cc0'::uuid, 'ACCUMULATED DEPRECIATION', 'DEPRECIATION', '756294b0-6f4e-4284-8ef6-a8df55036739'::uuid, 'OTHERENTITIES', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'com.hisd3.hismk2.domain.inventory.AccountingCategory', 'admin', '2022-09-09 14:34:23.630', 'admin', '2022-09-09 14:34:23.630', NULL, NULL, NULL , NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.subaccount_setup WHERE id = '0869f239-296e-4ca9-a03f-9e392dca8cc0'
  );
