INSERT INTO "accounting"."chart_of_accounts" ("id", "account_code", "description", "category", "tags", "parent", "deprecated", "created_by", "created_date", "last_modified_by", "last_modified_date", "deleted", "account_type", "fs_type", "normal_side", "is_contra") VALUES
('9137a77c-ff96-44cd-89f1-7e5c89346068', '100165', 'INPUT VAT', NULL, NULL, NULL, NULL, NULL, '2020-10-20 11:19:48.234', NULL, '2020-10-20 11:19:48.234', NULL, 'ASSET', 'BALANCE_SHEET', 'DEBIT', 'f');

INSERT INTO "accounting"."chart_of_accounts" ("id", "account_code", "description", "category", "tags", "parent", "deprecated", "created_by", "created_date", "last_modified_by", "last_modified_date", "deleted", "account_type", "fs_type", "normal_side", "is_contra") VALUES
('79356d75-772b-4978-abc3-35208cd54277', '100075', 'ADVANCES TO EMPLOYEES', NULL, NULL, NULL, NULL, NULL, '2020-10-20 11:19:48.234', NULL, '2020-10-20 11:19:48.234', NULL, 'ASSET', 'BALANCE_SHEET', 'DEBIT', 'f');


INSERT INTO "accounting"."chart_of_accounts" ("id", "account_code", "description", "category", "tags", "parent", "deprecated", "created_by", "created_date", "last_modified_by", "last_modified_date", "deleted", "account_type", "fs_type", "normal_side", "is_contra") VALUES
('992d21de-f491-4a8b-a2e2-9a16a93ef780', '100155', 'ENGINEERING TOOLS AND SUPPLIES', NULL, NULL, NULL, NULL, NULL, '2020-10-20 11:19:48.234', NULL, '2020-10-20 11:19:48.234', NULL, 'ASSET', 'BALANCE_SHEET', 'DEBIT', 'f');


INSERT INTO "accounting"."subaccount_setup" ("id", "description", "subaccount_code", "subaccount_parent", "subaccount_type", "include_department", "attr_beginning_balance", "attr_credit_memo_adj", "attr_accrual_of_income", "attr_non_trade_cash_receipts", "attr_include_posting_accrued_income_multiple_customer", "attr_vatable", "attr_inactive", "attr_expense_account", "attr_debit_memo_adjustment", "attr_accrual_expense", "source_domain", "created_by", "created_date", "last_modified_by", "last_modified_date", "journal_placement", "category", "require_remarks", "attached_value") VALUES
('1d322cbc-4349-470c-8e56-7ce623b209dc', 'INVENTORY', '1010', NULL, 'OTHERENTITIES', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 'admin', '2020-12-06 11:13:00.208', 'admin', '2020-12-06 11:21:46.872', NULL, NULL, NULL, NULL);

INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('c85380d2-df89-42f2-816d-3712e61fff4d', '1d322cbc-4349-470c-8e56-7ce623b209dc', '1b8b9b4d-6054-4167-b6ad-3716e23838cc', 'admin', '2020-12-06 11:21:46.872', 'admin', '2020-12-06 11:21:46.872');



update "accounting"."subaccount_setup" set "subaccount_code" = '1010', "subaccount_parent"='1d322cbc-4349-470c-8e56-7ce623b209dc'  where id = '983c2619-3c37-4c74-8cd2-bc2f70b9390b';


delete  from "accounting"."subaccount_mother_accounts" where sub_account = '983c2619-3c37-4c74-8cd2-bc2f70b9390b';

INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('e0911102-5a9f-4183-8de9-826669f8cb8c', '983c2619-3c37-4c74-8cd2-bc2f70b9390b', '1b8b9b4d-6054-4167-b6ad-3716e23838cc', 'admin', '2020-12-06 11:32:31.452', 'admin', '2020-12-06 11:32:31.452');


update "accounting"."subaccount_setup" set "subaccount_code" = '1020', "subaccount_parent"='1d322cbc-4349-470c-8e56-7ce623b209dc'  where id = '086b650b-94a7-47fe-bc73-ab2e3d5c335b';

delete  from "accounting"."subaccount_mother_accounts" where sub_account = '086b650b-94a7-47fe-bc73-ab2e3d5c335b';

INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('1ab43121-ede8-49d7-9132-7b5c06ccf447', '086b650b-94a7-47fe-bc73-ab2e3d5c335b', '1b8b9b4d-6054-4167-b6ad-3716e23838cc', 'admin', '2020-12-06 11:32:31.452', 'admin', '2020-12-06 11:32:31.452');



INSERT INTO "accounting"."subaccount_setup" ("id", "description", "subaccount_code", "subaccount_parent", "subaccount_type", "include_department", "attr_beginning_balance", "attr_credit_memo_adj", "attr_accrual_of_income", "attr_non_trade_cash_receipts", "attr_include_posting_accrued_income_multiple_customer", "attr_vatable", "attr_inactive", "attr_expense_account", "attr_debit_memo_adjustment", "attr_accrual_expense", "source_domain", "created_by", "created_date", "last_modified_by", "last_modified_date", "journal_placement", "category", "require_remarks", "attached_value") VALUES
('9d3cbd3c-2f11-4d7c-959b-463a72798dd6', 'LINENS', '1020', NULL, 'ASSETCLASS', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 15:02:04.404', 'admin', '2020-12-06 15:02:04.404', NULL, NULL, NULL, NULL),
('ea9fa63f-81b9-451d-a759-a2b9d363a987', 'CURTAINS', '1010', NULL, 'ASSETCLASS', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 14:59:04.154', 'admin', '2020-12-06 14:59:04.154', NULL, NULL, NULL, NULL);


INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('b7a359fb-e97f-4c9e-8832-17609a2834bb', '9d3cbd3c-2f11-4d7c-959b-463a72798dd6', 'aa56e6ab-19cb-43fd-ae1c-aa306b358de5', 'admin', '2020-12-06 15:02:04.406', 'admin', '2020-12-06 15:02:04.406');


INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('77f235f2-599f-4160-9142-474f72abffef', 'ea9fa63f-81b9-451d-a759-a2b9d363a987', 'aa56e6ab-19cb-43fd-ae1c-aa306b358de5', 'admin', '2020-12-06 14:59:04.163', 'admin', '2020-12-06 14:59:04.163');




INSERT INTO "accounting"."subaccount_setup" ("id", "description", "subaccount_code", "subaccount_parent", "subaccount_type", "include_department", "attr_beginning_balance", "attr_credit_memo_adj", "attr_accrual_of_income", "attr_non_trade_cash_receipts", "attr_include_posting_accrued_income_multiple_customer", "attr_vatable", "attr_inactive", "attr_expense_account", "attr_debit_memo_adjustment", "attr_accrual_expense", "source_domain", "created_by", "created_date", "last_modified_by", "last_modified_date", "journal_placement", "category", "require_remarks", "attached_value") VALUES
('d6e1831b-355e-4d85-834d-58538cb5c1f4', 'OTHERS', '1030', NULL, 'ASSETCLASS', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 15:10:15.374', 'admin', '2020-12-06 15:10:15.374', NULL, NULL, NULL, NULL),
('fd63159e-3bbb-4e4c-b38d-bf31263b248e', 'DINING', '1020', NULL, 'ASSETCLASS', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 15:09:42.528', 'admin', '2020-12-06 15:09:42.528', NULL, NULL, NULL, NULL),
('203e5689-bedd-4cde-ab5e-f10681806826', 'DIETARY', '1010', NULL, 'ASSETCLASS', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 15:09:11.215', 'admin', '2020-12-06 15:09:11.215', NULL, NULL, NULL, NULL);



INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('21f53a77-b292-473f-8b42-0a8850aad9b5', 'd6e1831b-355e-4d85-834d-58538cb5c1f4', '80a97c1f-8728-432c-af35-2bf73a79055c', 'admin', '2020-12-06 15:10:15.377', 'admin', '2020-12-06 15:10:15.377');

INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('bbf6a277-f273-43de-80ce-20357dfbf089', 'fd63159e-3bbb-4e4c-b38d-bf31263b248e', '80a97c1f-8728-432c-af35-2bf73a79055c', 'admin', '2020-12-06 15:09:42.53', 'admin', '2020-12-06 15:09:42.53');

INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('c871b9d3-21b0-4303-a3f3-218d4dac360b', '203e5689-bedd-4cde-ab5e-f10681806826', '80a97c1f-8728-432c-af35-2bf73a79055c', 'admin', '2020-12-06 15:09:11.219', 'admin', '2020-12-06 15:09:11.219');


UPDATE "accounting"."subaccount_setup" SET "attr_inactive" = true WHERE "id" = 'ac5f0b95-d92d-4902-97a4-8ee874e153e4';

UPDATE "accounting"."chart_of_accounts" SET "deprecated" = 't' WHERE "id" = 'b25646d5-fe9d-48ba-ba7e-171e55818034';



INSERT INTO "accounting"."subaccount_setup" ("id", "description", "subaccount_code", "subaccount_parent", "subaccount_type", "include_department", "attr_beginning_balance", "attr_credit_memo_adj", "attr_accrual_of_income", "attr_non_trade_cash_receipts", "attr_include_posting_accrued_income_multiple_customer", "attr_vatable", "attr_inactive", "attr_expense_account", "attr_debit_memo_adjustment", "attr_accrual_expense", "source_domain", "created_by", "created_date", "last_modified_by", "last_modified_date", "journal_placement", "category", "require_remarks", "attached_value") VALUES
('cba9cc8a-873c-436b-9ce8-f33644b1a513', 'UNUSED SUPPLIES', '1030', NULL, 'ASSETCLASS', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 15:45:42.051', 'admin', '2020-12-06 15:45:42.051', NULL, NULL, NULL, NULL),
('bffd0407-d9bf-4a06-aefa-cdf17b2e014f', 'UNUSED OFFICE SUPPLIES', '1020', NULL, 'ASSETCLASS', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 15:45:02.515', 'admin', '2020-12-06 15:45:02.515', NULL, NULL, NULL, NULL),
('c7cc4d03-fffa-4f8e-bac5-19a87f585717', 'CREDITABLE WITHOLDING TAX', '1010', NULL, 'ASSETCLASS', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 15:44:33.829', 'admin', '2020-12-06 15:44:33.829', NULL, NULL, NULL, NULL);

INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('4b652c90-831a-429c-b29e-f6fd1a6f0151', 'cba9cc8a-873c-436b-9ce8-f33644b1a513', '5f0df9b2-f49a-4abf-9f4d-7c5d92eaf3c7', 'admin', '2020-12-06 15:46:28.254', 'admin', '2020-12-06 15:46:28.254');
INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('767e96e1-8847-485e-b049-d46bd2717a7e', 'bffd0407-d9bf-4a06-aefa-cdf17b2e014f', '5f0df9b2-f49a-4abf-9f4d-7c5d92eaf3c7', 'admin', '2020-12-06 15:45:02.517', 'admin', '2020-12-06 15:45:02.517');
INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('63c08b1e-7700-4ef9-a9ee-a48bfd0853e6', 'c7cc4d03-fffa-4f8e-bac5-19a87f585717', '5f0df9b2-f49a-4abf-9f4d-7c5d92eaf3c7', 'admin', '2020-12-06 15:44:33.832', 'admin', '2020-12-06 15:44:33.832');



INSERT INTO "accounting"."subaccount_setup" ("id", "description", "subaccount_code", "subaccount_parent", "subaccount_type", "include_department", "attr_beginning_balance", "attr_credit_memo_adj", "attr_accrual_of_income", "attr_non_trade_cash_receipts", "attr_include_posting_accrued_income_multiple_customer", "attr_vatable", "attr_inactive", "attr_expense_account", "attr_debit_memo_adjustment", "attr_accrual_expense", "source_domain", "created_by", "created_date", "last_modified_by", "last_modified_date", "journal_placement", "category", "require_remarks", "attached_value") VALUES
('1fef3116-6122-4c57-8f19-93b5367ba6b7', 'SSS MATERNITY BENEFITS', '1040', NULL, 'OTHERENTITIES', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 'admin', '2020-12-06 15:51:55.336', 'admin', '2020-12-06 15:51:55.336', NULL, NULL, NULL, NULL);
INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('ffa9f04d-6996-48a1-8cd7-5a860413ad01', '1fef3116-6122-4c57-8f19-93b5367ba6b7', 'f1a535f0-2c57-4824-a3c2-196342e85f30', 'admin', '2020-12-06 15:51:55.342', 'admin', '2020-12-06 15:51:55.342');



INSERT INTO "accounting"."subaccount_setup" ("id", "description", "subaccount_code", "subaccount_parent", "subaccount_type", "include_department", "attr_beginning_balance", "attr_credit_memo_adj", "attr_accrual_of_income", "attr_non_trade_cash_receipts", "attr_include_posting_accrued_income_multiple_customer", "attr_vatable", "attr_inactive", "attr_expense_account", "attr_debit_memo_adjustment", "attr_accrual_expense", "source_domain", "created_by", "created_date", "last_modified_by", "last_modified_date", "journal_placement", "category", "require_remarks", "attached_value") VALUES
('5cc06b3d-6846-43bb-b7ff-2556444a869a', 'VEHICLE', '1030', NULL, 'ADJUSTMENTS', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 15:56:10.204', 'admin', '2020-12-06 15:56:10.204', NULL, NULL, NULL, NULL);
INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('8a4b4c05-0df6-4a70-aad0-23a29d8ab823', '5cc06b3d-6846-43bb-b7ff-2556444a869a', 'c6a9c8bb-e95a-4773-aa30-a21d896947ea', 'admin', '2020-12-06 15:56:51.972', 'admin', '2020-12-06 15:56:51.972');


INSERT INTO "accounting"."subaccount_setup" ("id", "description", "subaccount_code", "subaccount_parent", "subaccount_type", "include_department", "attr_beginning_balance", "attr_credit_memo_adj", "attr_accrual_of_income", "attr_non_trade_cash_receipts", "attr_include_posting_accrued_income_multiple_customer", "attr_vatable", "attr_inactive", "attr_expense_account", "attr_debit_memo_adjustment", "attr_accrual_expense", "source_domain", "created_by", "created_date", "last_modified_by", "last_modified_date", "journal_placement", "category", "require_remarks", "attached_value") VALUES
('831277cc-9036-4146-b310-4f5d882bcd0f', 'INCOME TAX', '1010', NULL, 'ADJUSTMENTS', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 15:59:22.129', 'admin', '2020-12-06 15:59:22.129', NULL, NULL, NULL, NULL);


INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('39c4cf2d-85f5-42bc-922e-ba13ef982ff1', '831277cc-9036-4146-b310-4f5d882bcd0f', '0a985a56-6f2e-474b-9007-61fba96a0379', 'admin', '2020-12-06 15:59:22.131', 'admin', '2020-12-06 15:59:22.131');




INSERT INTO "accounting"."chart_of_accounts" ("id", "account_code", "description", "category", "tags", "parent", "deprecated", "created_by", "created_date", "last_modified_by", "last_modified_date", "deleted", "account_type", "fs_type", "normal_side", "is_contra") VALUES
('9137a77c-ff96-44cd-89f1-7e5c89346068', '100165', 'INPUT VAT', NULL, NULL, NULL, NULL, NULL, '2020-10-20 11:19:48.234', NULL, '2020-10-20 11:19:48.234', NULL, 'ASSET', 'BALANCE_SHEET', 'DEBIT', 'f');



INSERT INTO "accounting"."chart_of_accounts" ("id", "account_code", "description", "category", "tags", "parent", "deprecated", "created_by", "created_date", "last_modified_by", "last_modified_date", "deleted", "account_type", "fs_type", "normal_side", "is_contra") VALUES
('724cee20-70b1-46a3-987b-746452190581', '100360', 'ALLOWANCE FOR CREDIT LOSSES', NULL, NULL, NULL, NULL, NULL, '2020-10-20 11:19:48.234', NULL, '2020-10-20 11:19:48.234', NULL, 'ASSET', 'BALANCE_SHEET', 'CREDIT', 't');


--- working capital ---
delete from "accounting"."subaccount_mother_accounts" where "sub_account" = '1cea38a7-c814-4a68-ac5f-f3c90f002102';


INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('0d165e28-f62d-4004-916b-893b775e07c1', '1cea38a7-c814-4a68-ac5f-f3c90f002102', '3d00077a-9dc1-4a43-9ed6-8bbb1737bdd9', 'admin', '2020-12-06 16:15:07.967', 'admin', '2020-12-06 16:15:07.967'),
('84558482-b2ac-4e19-9a0b-ecaf4b21160f', '1cea38a7-c814-4a68-ac5f-f3c90f002102', 'b26d08e0-e7b5-43c1-8d01-faa878cd8e97', 'admin', '2020-12-06 16:15:07.967', 'admin', '2020-12-06 16:15:07.967'),
('8ae9c12a-25e0-4432-b78d-e244ffb5c39e', '1cea38a7-c814-4a68-ac5f-f3c90f002102', 'fff4ace4-d924-42e9-93ee-ce7f15d5b4a8', 'admin', '2020-12-06 16:15:07.967', 'admin', '2020-12-06 16:15:07.967');




INSERT INTO "accounting"."chart_of_accounts" ("id", "account_code", "description", "category", "tags", "parent", "deprecated", "created_by", "created_date", "last_modified_by", "last_modified_date", "deleted", "account_type", "fs_type", "normal_side", "is_contra") VALUES
('a083c635-7e01-465d-b110-d3f1ec0f1d7c', '200065', 'SALES TAX', NULL, NULL, NULL, NULL, NULL, '2020-10-20 11:19:48.234', NULL, '2020-10-20 11:19:48.234', NULL, 'LIABILITY', 'BALANCE_SHEET', 'CREDIT', 'f');

INSERT INTO "accounting"."subaccount_setup" ("id", "description", "subaccount_code", "subaccount_parent", "subaccount_type", "include_department", "attr_beginning_balance", "attr_credit_memo_adj", "attr_accrual_of_income", "attr_non_trade_cash_receipts", "attr_include_posting_accrued_income_multiple_customer", "attr_vatable", "attr_inactive", "attr_expense_account", "attr_debit_memo_adjustment", "attr_accrual_expense", "source_domain", "created_by", "created_date", "last_modified_by", "last_modified_date", "journal_placement", "category", "require_remarks", "attached_value") VALUES
('ad314557-b26e-44f0-bca4-b544a9dd69f9', 'FOUNDERS', '2000', NULL, 'EXPENSE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 16:26:00.894', 'admin', '2020-12-06 16:26:41.14', NULL, NULL, NULL, NULL);

INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('5cd952c6-a256-4f05-b7af-c61665d51035', 'ad314557-b26e-44f0-bca4-b544a9dd69f9', '6293abdd-cdbe-4e0a-8b23-3334f8e460be', 'admin', '2020-12-06 16:26:41.14', 'admin', '2020-12-06 16:26:41.14');





INSERT INTO "accounting"."chart_of_accounts" ("id", "account_code", "description", "category", "tags", "parent", "deprecated", "created_by", "created_date", "last_modified_by", "last_modified_date", "deleted", "account_type", "fs_type", "normal_side", "is_contra") VALUES
('5e847eb7-6332-4abf-bb21-89952419cf4b', '200195', 'ADVANCES FROM SHAREHOLDERS', NULL, NULL, NULL, NULL, NULL, '2020-10-20 11:19:48.234', NULL, '2020-10-20 11:19:48.234', NULL, 'LIABILITY', 'BALANCE_SHEET', 'CREDIT', 'f');



INSERT INTO "accounting"."subaccount_setup" ("id", "description", "subaccount_code", "subaccount_parent", "subaccount_type", "include_department", "attr_beginning_balance", "attr_credit_memo_adj", "attr_accrual_of_income", "attr_non_trade_cash_receipts", "attr_include_posting_accrued_income_multiple_customer", "attr_vatable", "attr_inactive", "attr_expense_account", "attr_debit_memo_adjustment", "attr_accrual_expense", "source_domain", "created_by", "created_date", "last_modified_by", "last_modified_date", "journal_placement", "category", "require_remarks", "attached_value") VALUES
('fa23e1df-7c47-4e60-8198-01fd59434f01', 'DUE TO FOUNDERS', '1020', NULL, 'EXPENSE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 16:33:44.181', 'admin', '2020-12-06 16:33:44.181', NULL, NULL, NULL, NULL),
('34a304c8-038f-44cc-9845-45545797e3d8', 'ADVANCES FROM NON-FOUNDERS', '1010', NULL, 'EXPENSE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-06 16:33:20.711', 'admin', '2020-12-06 16:33:20.711', NULL, NULL, NULL, NULL);

INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('bacaa3fb-08ab-4552-92bb-3b3fb6f0813f', 'fa23e1df-7c47-4e60-8198-01fd59434f01', '5e847eb7-6332-4abf-bb21-89952419cf4b', 'admin', '2020-12-06 16:33:44.183', 'admin', '2020-12-06 16:33:44.183');

INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('86c08686-1ac8-4b9f-995b-3a849e023d9f', '34a304c8-038f-44cc-9845-45545797e3d8', '5e847eb7-6332-4abf-bb21-89952419cf4b', 'admin', '2020-12-06 16:33:20.714', 'admin', '2020-12-06 16:33:20.714');


UPDATE "accounting"."chart_of_accounts" SET "description" = 'ADDITIONAL PAID IN CAPITAL' WHERE "id" = '36e57324-5aad-412c-bba0-62ca3f014369';



UPDATE "accounting"."chart_of_accounts" SET "deprecated" = 't' WHERE "id" = '4ca33f40-d3df-4423-a31e-78472f6a7835';

UPDATE "accounting"."chart_of_accounts" SET "description" = 'RETAINED EARNINGS (DEFICIT)' WHERE "id" = '7447bf5e-9693-4f2c-b762-88f7eac4442b';


INSERT INTO "accounting"."chart_of_accounts" ("id", "account_code", "description", "category", "tags", "parent", "deprecated", "created_by", "created_date", "last_modified_by", "last_modified_date", "deleted", "account_type", "fs_type", "normal_side", "is_contra") VALUES
('429d68a9-3c47-48bf-888a-47caa3e4538f', '300060', 'CURRENT YEAR EARNINGS', NULL, NULL, NULL, NULL, NULL, '2020-10-20 11:19:48.234', NULL, '2020-10-20 11:19:48.234', NULL, 'EQUITY', 'BALANCE_SHEET', 'CREDIT', 'f');


INSERT INTO "accounting"."chart_of_accounts" ("id", "account_code", "description", "category", "tags", "parent", "deprecated", "created_by", "created_date", "last_modified_by", "last_modified_date", "deleted", "account_type", "fs_type", "normal_side", "is_contra") VALUES
('2164c7c9-6bd0-4f45-94fb-1a9287bb902b', '500005', 'COST OF SALES', NULL, NULL, NULL, NULL, NULL, '2020-10-20 11:19:48.234', NULL, '2020-10-20 11:19:48.234', NULL, 'COST_OF_SALE', 'INCOME', 'DEBIT', 'f');

