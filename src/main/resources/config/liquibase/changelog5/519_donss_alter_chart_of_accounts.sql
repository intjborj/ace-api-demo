INSERT INTO accounting.chart_of_accounts(id,account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted, account_type, fs_type, normal_side, is_contra)
SELECT uuid_generate_v4(), '600610', 'PAYABLE TO INVESTOR', NULL, NULL, NULL, NULL, NULL, NOW(), NULL, NOW(), NULL, 'LIABILITY', 'INCOME', 'CREDIT', false
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.chart_of_accounts WHERE description = 'PAYABLE TO INVESTOR'
  );

INSERT INTO accounting.subaccount_setup(id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value,department_includes)
SELECT uuid_generate_v4(), 'PAYABLE TO INVESTOR','3700',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,false,NULL,NULL,'admin',NOW(),'admin',NOW(),NULL,NULL,NULL,NULL,NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.subaccount_setup WHERE description = 'PAYABLE TO INVESTOR'
  );