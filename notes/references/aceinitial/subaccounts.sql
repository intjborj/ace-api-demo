delete from accounting.subaccount_setup;

INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('0f3b746d-dc0d-4c61-b0d1-40850971cc64','SCRAP SALES','1010',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 03:38:49.039','admin','2020-10-20 03:38:49.039',NULL,NULL,NULL,NULL)
,('2fed1c3e-d502-4975-97f8-a29ee13c1d96','WIFI USE','1020',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 03:41:03.865','admin','2020-10-20 03:41:03.865',NULL,NULL,NULL,NULL)
,('4daffa7c-8651-4a06-b05d-ce150c824e33','SHOOTING LOCATION','1030',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 03:42:44.927','admin','2020-10-20 03:42:44.927',NULL,NULL,NULL,NULL)
,('dc69e9e7-e5bf-46c4-9331-330dd4e6dc07','PROMISSORY NOTES PAYMENTS','1010','c1b6638e-141b-41fb-807d-cb4a8ce60c73','OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 04:36:41.636','admin','2020-10-20 04:36:41.636',NULL,NULL,NULL,NULL)
,('958ee192-dedc-4e48-9b37-7823d28a58f0','CASHIER''S OVERAGE','1040',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 04:39:49.381','admin','2020-10-20 04:39:49.381',NULL,NULL,NULL,NULL)
,('2d315e70-54a0-493a-be9f-b5bb23b2bc13','BANK CREDIT MEMOS','1050',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 04:40:42.516','admin','2020-10-20 04:40:42.516',NULL,NULL,NULL,NULL)
,('d5514ff3-7626-431c-b1c3-f8e1893b1cb5','SPONSORSHIP','1060',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 04:41:56.308','admin','2020-10-20 04:41:56.308',NULL,NULL,NULL,NULL)
,('f2aa1a65-c2de-4986-afd5-612c8209585e','SEMINARS','1070',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 04:42:20.065','admin','2020-10-20 04:42:20.065',NULL,NULL,NULL,NULL)
,('812a1257-9f59-443d-a411-d61ef05e3be4','ELECTRICITY','1080',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 04:43:10.117','admin','2020-10-20 04:43:10.117',NULL,NULL,NULL,NULL)
,('3248e907-0ae7-4972-a5a1-d2bcc30d3e4f','REBATES','1090',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 04:43:33.820','admin','2020-10-20 04:43:33.820',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('72ad18a1-eb25-4a92-8168-2ed307c25225','CONSIGNMENT','2010',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 04:44:01.119','admin','2020-10-20 04:44:45.191',NULL,NULL,NULL,NULL)
,('b7d78cca-3dfb-486b-ad18-f8d5d17ff864','RENTAL COMMERCIAL SPACE','2020',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 04:45:10.679','admin','2020-10-20 04:45:10.679',NULL,NULL,NULL,NULL)
,('7f04e034-7a8a-48b3-bdc9-279a8beead8c','EMPTY GALLONS','2030',NULL,'OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 05:01:26.978','admin','2020-10-20 05:01:26.978',NULL,NULL,NULL,NULL)
,('9894f3ce-c956-4456-8bfb-d8e92db2a31d','CASH ON HAND CASHER TERMINAL ASSIGN.','COH_CTMNL',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'com.hisd3.hismk2.domain.cashiering.CashierTerminal','admin','2020-10-20 07:10:55.040','admin','2020-10-20 07:11:08.455',NULL,NULL,NULL,NULL)
,('ca08d089-c633-4a13-8967-2e40d72a9b80','CHECK ON HAND CASHIER TERMINALS ASSGN.','CKOH_CTMNL',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'com.hisd3.hismk2.domain.cashiering.CashierTerminal','admin','2020-10-20 07:12:14.981','admin','2020-10-20 07:12:31.587',NULL,NULL,NULL,NULL)
,('6fa673c4-86cc-48ec-95a7-1e2f793e0f61','PETTY CASH SETUP','PCF_CTMNL',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'com.hisd3.hismk2.domain.cashiering.CashierTerminal','admin','2020-10-20 11:34:53.350','admin','2020-10-20 11:34:53.350',NULL,NULL,NULL,NULL)
,('ba31ae6d-5a8f-4f08-9446-75d3e8e991e3','CHANGE FUND SETUP','CF_CTML',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'com.hisd3.hismk2.domain.cashiering.CashierTerminal','admin','2020-10-20 11:36:22.109','admin','2020-10-20 11:36:22.109',NULL,NULL,NULL,NULL)
,('f78f76c8-4d70-4755-9d66-1579eb1d00b5','CASH IN BANK SETUP','CIB_BANK',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'com.hisd3.hismk2.domain.accounting.Bank','admin','2020-10-20 11:37:27.973','admin','2020-10-20 11:37:27.973',NULL,NULL,NULL,NULL)
,('2ddb1f76-9b58-47af-b68c-0995708f0985','KITCHENWARES, TABLE WARES AND OTHER UTENSILS','2000',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:07:18.091','admin','2020-10-21 03:07:18.091',NULL,NULL,NULL,NULL)
,('a7ad1076-0eac-4227-873a-b12a38e8530a','IN-PATIENT','1010','19ec0410-8aa1-4366-91fe-fb688cb4c261','INCOME',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 23:53:39.002','admin','2020-10-20 23:53:39.002',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('0ebf62c5-6da6-4c71-ad64-671f757c23e6','OUTPATIENT','1020','19ec0410-8aa1-4366-91fe-fb688cb4c261','INCOME',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 23:54:17.256','admin','2020-10-20 23:54:17.256',NULL,NULL,NULL,NULL)
,('18a2606b-2821-4421-a185-b93dc63a87a7','EMERGENCY PATIENT','1030','19ec0410-8aa1-4366-91fe-fb688cb4c261','INCOME',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 23:54:47.045','admin','2020-10-20 23:54:47.045',NULL,NULL,NULL,NULL)
,('ac32e4fa-0dfe-411d-9e41-7e82ed069840','OTC PATIENT','1040','19ec0410-8aa1-4366-91fe-fb688cb4c261','INCOME',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-20 23:55:37.002','admin','2020-10-20 23:55:37.002',NULL,NULL,NULL,NULL)
,('e7639e17-0793-44f3-bd97-3f3e42b22174','CIB CLEARING ACCOUNT','CIB_CLEARING','34f692e5-66f3-4f80-9a9d-2a241a212913','OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'com.hisd3.hismk2.domain.accounting.Bank','admin','2020-10-20 23:57:14.263','admin','2020-10-20 23:57:14.263',NULL,NULL,NULL,NULL)
,('2d056ddb-c2d0-411b-ad0c-30d753c0d557','CREDIT CARD PAYMENTS AR SETUP','ARRCREDITCARD','20b5b8b6-d279-4578-870f-ebb3f3d6e17e','OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'com.hisd3.hismk2.domain.accounting.Bank','admin','2020-10-21 00:28:08.034','admin','2020-10-21 00:28:08.034',NULL,NULL,NULL,NULL)
,('6d334abb-9aff-4923-a0ab-75987a2e5a3d','AR CORP SETUP','AR_CORP','2225eac1-261a-4c3b-a13d-b4d1578accb5','OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'com.hisd3.hismk2.domain.billing.CompanyAccount','admin','2020-10-21 02:01:33.776','admin','2020-10-21 02:01:33.776',NULL,NULL,NULL,NULL)
,('db26e933-cd09-4f40-b054-f40d73caf4db','REVOLVING FUND SETUP','RF_CTRML',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'com.hisd3.hismk2.domain.cashiering.CashierTerminal','admin','2020-10-21 02:03:03.921','admin','2020-10-21 02:03:03.921',NULL,NULL,NULL,NULL)
,('c1b6638e-141b-41fb-807d-cb4a8ce60c73','UNEARNED REVENUE','1010',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','admin','2020-10-20 03:47:10.312','admin','2020-10-21 02:09:05.125',NULL,NULL,NULL,NULL)
,('19ec0410-8aa1-4366-91fe-fb688cb4c261','PATIENT','1010',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','admin','2020-10-20 11:41:20.751','admin','2020-10-21 02:09:10.338',NULL,NULL,NULL,NULL)
,('34f692e5-66f3-4f80-9a9d-2a241a212913','CLEARING','1010',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','admin','2020-10-20 23:56:35.818','admin','2020-10-21 02:09:15.168',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('2225eac1-261a-4c3b-a13d-b4d1578accb5','INSURANCES/HMO/CORPORATE','1030',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','admin','2020-10-20 23:52:12.576','admin','2020-10-21 02:09:19.833',NULL,NULL,NULL,NULL)
,('20b5b8b6-d279-4578-870f-ebb3f3d6e17e','CREDIT CARD','1020',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','admin','2020-10-20 23:47:18.222','admin','2020-10-21 02:09:23.697',NULL,NULL,NULL,NULL)
,('5e25dce5-d660-4924-ab87-e4bcb16770cb','EMPLOYEE','1010',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','admin','2020-10-21 02:10:23.745','admin','2020-10-21 02:10:23.745',NULL,NULL,NULL,NULL)
,('7c41ddb3-6409-4c82-aec4-ff929fe9fbf5','ADMIN','1020',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','admin','2020-10-21 02:10:55.669','admin','2020-10-21 02:10:55.669',NULL,NULL,NULL,NULL)
,('c0357732-1c08-44e6-8f7d-b56edc11495c','DOCTORS','1030',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','admin','2020-10-21 02:11:34.899','admin','2020-10-21 02:11:34.899',NULL,NULL,NULL,NULL)
,('ab251127-2a6a-409f-b663-ef233c9d3cd4','FOUNDERS','1010',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','admin','2020-10-21 02:13:16.022','admin','2020-10-21 02:13:16.022',NULL,NULL,NULL,NULL)
,('2feb74bb-31bf-4000-8e0c-7c85ee418b6b','NON-FOUNDERS','1020',NULL,'OTHERENTITIES',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'','admin','2020-10-21 02:13:32.424','admin','2020-10-21 02:13:32.424',NULL,NULL,NULL,NULL)
,('49b2ce81-9daf-4963-b522-652f49cffcb5','DRUGS AND MEDICINE','1010',NULL,'ASSETCLASS',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 02:53:08.054','admin','2020-10-21 02:53:08.054',NULL,NULL,NULL,NULL)
,('f67caa55-98ac-47f4-b767-d2e81179a23c','MEDICAL SUPPLIES','1020',NULL,'ASSETCLASS',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 02:53:53.148','admin','2020-10-21 02:53:53.148',NULL,NULL,NULL,NULL)
,('9134c307-589f-4f23-bee7-a5140c3afeb7','OXYGEN','1030',NULL,'ASSETCLASS',false,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 02:59:48.377','admin','2020-10-21 02:59:48.377',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('983c2619-3c37-4c74-8cd2-bc2f70b9390b','FND-DIETARY','1040',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:03:18.671','admin','2020-10-21 03:03:18.671',NULL,NULL,NULL,NULL)
,('086b650b-94a7-47fe-bc73-ab2e3d5c335b','FND-DINING','1050',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:03:34.219','admin','2020-10-21 03:03:40.367',NULL,NULL,NULL,NULL)
,('5837803e-802b-4fad-9205-326cec16c2a6','BLOOD BANK','1060',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:05:48.810','admin','2020-10-21 03:05:48.810',NULL,NULL,NULL,NULL)
,('e1f1ebe5-20d1-4b51-acb8-e79a2dd52672','ADMITTING','1070',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:06:10.982','admin','2020-10-21 03:06:10.982',NULL,NULL,NULL,NULL)
,('aa995e87-f3c8-4ea0-9ab8-7f84dd0136be','PATIENT GOWN','1080',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:06:25.919','admin','2020-10-21 03:06:25.919',NULL,NULL,NULL,NULL)
,('84b2adef-536a-4507-9fbd-951367758fb9','GENERAL HOSPITAL MAINTENANCE SUPPLIES','1090',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:07:03.212','admin','2020-10-21 03:07:03.212',NULL,NULL,NULL,NULL)
,('2f40a016-17b2-43e2-b52a-9933cd57c8a7','BUILDING','1010',NULL,'ADJUSTMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:11:03.541','admin','2020-10-21 03:11:03.541','DEBIT',NULL,NULL,NULL)
,('ece436e5-fcbe-45b5-b497-5830e9c78a22','EQUIPMENT','1020',NULL,'ADJUSTMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:11:43.320','admin','2020-10-21 03:11:43.320','DEBIT',NULL,NULL,NULL)
,('a336ade5-0631-4fd7-9ca2-f7c02849817f','LAND','1010',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:39:30.881','admin','2020-10-21 03:39:30.881',NULL,NULL,NULL,NULL)
,('16039209-2027-4573-b8f3-ddf777478943','LAND IMPROVEMENT','1020',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:41:34.328','admin','2020-10-21 03:41:34.328',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('8887fa94-05b6-4d02-acf3-d151550fd942','OTHER LAND IMPROVEMENT','1030',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 03:42:24.504','admin','2020-10-21 03:42:24.504',NULL,NULL,NULL,NULL)
,('d26eb949-c416-44d3-adbf-86c535cc8ac4','HOSPITAL BUILDING','1040',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:06:22.694','admin','2020-10-21 04:06:22.694',NULL,NULL,NULL,NULL)
,('e82cf7f4-b67b-49c2-aa3b-1e16fa9550b2','HOSPITAL BUILDING ELEVATOR','1050',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:07:06.342','admin','2020-10-21 04:07:06.342',NULL,NULL,NULL,NULL)
,('fa50a4ff-43c9-4e0e-a0ad-6df564164159','HOSPITAL BUILDING POWER HOUSE','1060',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:07:54.192','admin','2020-10-21 04:07:54.192',NULL,NULL,NULL,NULL)
,('2de2f4bc-00db-4865-9369-089464e32ab3','HOSPITAL BUILDING WATERPUMP','1070',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:08:15.192','admin','2020-10-21 04:08:15.192',NULL,NULL,NULL,NULL)
,('d2db8a67-64fe-488c-9b53-0161b10a3129','HOSPITAL BUILDING LAUNDRY HOUSE','1090',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:45:14.295','admin','2020-10-21 04:45:14.295',NULL,NULL,NULL,NULL)
,('dab17b28-fc8c-4bed-9516-b120d5e41466','LEASEHOLD IMPROVEMENTS','2000',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:45:35.816','admin','2020-10-21 04:45:35.816',NULL,NULL,NULL,NULL)
,('7a11a47e-7e3e-42a1-8f78-7b1c1a375d32','HOSPITAL EQUIPMENT','2010',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:46:16.983','admin','2020-10-21 04:46:16.983',NULL,NULL,NULL,NULL)
,('5ad0dbe4-61db-45b5-8459-194b4167c558','MEDICAL EQUIPMENT','2020',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:46:44.538','admin','2020-10-21 04:46:44.538',NULL,NULL,NULL,NULL)
,('7c5cc96b-4054-4dd4-809e-a9191c5eec46','TRANSPORTATION/MOBILE EQUIPMENT','2030',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:47:17.090','admin','2020-10-21 04:47:17.090',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('470d7db3-ed81-4b00-b3ca-b08e9775cced','OFFICE EQUIPMENT','2040',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:47:40.230','admin','2020-10-21 04:47:40.230',NULL,NULL,NULL,NULL)
,('bdc695bc-e60b-4536-aa40-f28df5a1c3bd','HOSPITAL FURNITURES & FIXTURES','2050',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:48:11.821','admin','2020-10-21 04:48:11.821',NULL,NULL,NULL,NULL)
,('42eff05a-b37c-4344-9651-347f2af21103','OFFICE FURNITURES & FIXTURES','2060',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:49:24.925','admin','2020-10-21 04:49:24.925',NULL,NULL,NULL,NULL)
,('34e85a26-b262-4201-b509-ed41f51e5329','TOOLS & KITCHEN EQUIPMENT-DIETARY','2070',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:50:28.497','admin','2020-10-21 04:50:28.497',NULL,NULL,NULL,NULL)
,('230ec858-8d4b-4a7b-82cf-2df899da4ce1','TOOLS & KITCHEN EQUIPMENT-DINING','2080',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:50:46.022','admin','2020-10-21 04:50:46.022',NULL,NULL,NULL,NULL)
,('76523ad0-1334-4cd7-9d88-628df805996f','BIOMED ENGINEERING','2090',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:51:02.227','admin','2020-10-21 04:51:02.227',NULL,NULL,NULL,NULL)
,('ac5785b7-ddb6-4ad9-972b-d7d1044ae737','GENERAL SERVICES','3000',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:51:34.083','admin','2020-10-21 04:51:34.083',NULL,NULL,NULL,NULL)
,('a1daa4a2-0b01-4ef5-a129-adb5733c6f04','ELECTRICAL AND INSTALLATION EQUIPMENT','3010',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:52:08.574','admin','2020-10-21 04:52:08.574',NULL,NULL,NULL,NULL)
,('1cf23bd5-5a8c-465d-8724-0619005b166c','SOFTWARE','1010',NULL,'ASSETCLASS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:52:48.295','admin','2020-10-21 04:52:48.295',NULL,NULL,NULL,NULL)
,('93c72d6e-1383-4253-ab30-752ed63df9f2','HOSPITAL BLDG','1010',NULL,'ADJUSTMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:58:28.752','admin','2020-10-21 04:58:28.752','DEBIT',NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('5256af7a-54fc-45c4-a52c-de6887e00106','POWERHOUSE','1020',NULL,'ADJUSTMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:58:40.978','admin','2020-10-21 04:58:40.978',NULL,NULL,NULL,NULL)
,('4b921b34-abfb-4799-9c4d-80d3f10cde30','WATERPUMP','1030',NULL,'ADJUSTMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:58:54.004','admin','2020-10-21 04:58:54.004',NULL,NULL,NULL,NULL)
,('dc60beba-2ab6-4bb3-96e7-b60715cc8f80','LAUNDRY HOUSE','1040',NULL,'ADJUSTMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 04:59:09.441','admin','2020-10-21 04:59:09.441',NULL,NULL,NULL,NULL)
,('ec255cde-a09d-40a2-93fc-30512876063a','REFUNDABLE DEPOSIT','1010',NULL,'ADJUSTMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:01:29.806','admin','2020-10-21 05:01:29.806',NULL,NULL,NULL,NULL)
,('6a45080f-df64-4ac7-afa0-1866daf78739','DEPOSIT HOLDOUT','1020',NULL,'ADJUSTMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:01:49.437','admin','2020-10-21 05:01:49.437',NULL,NULL,NULL,NULL)
,('456e2fa4-abb8-4884-8705-7f12c31bc9c2','OTHER PROPERTIES','1030',NULL,'ADJUSTMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:02:10.906','admin','2020-10-21 05:02:10.906',NULL,NULL,NULL,NULL)
,('232004cc-2390-4c48-a038-1454733dca07','PROGRESS BILLING','1010',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:21:09.567','admin','2020-10-21 05:21:09.567',NULL,NULL,NULL,NULL)
,('14e1a9aa-7cb7-41e3-8c8e-75f93153d30e','OTHER PDC''S','1020',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:21:25.898','admin','2020-10-21 05:21:25.898',NULL,NULL,NULL,NULL)
,('789395da-c78f-4933-99a5-05c1f193ccec','VENDORS AND SUPPLIERS','1030',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:22:16.410','admin','2020-10-21 05:22:16.410',NULL,NULL,NULL,NULL)
,('26536e0e-9409-431c-99e2-a3a66a09e8da','ROD PF LIABILITY','1040',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:22:42.397','admin','2020-10-21 05:22:42.397',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('502e4575-f49c-4583-a4a1-a0c90a92e09a','DOCTOR''S FEE LIABILITY','1050',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:23:01.964','admin','2020-10-21 05:23:01.964',NULL,NULL,NULL,NULL)
,('13938ae1-bcd5-4717-96f8-971137eca76a','READER''S FEE LIABILITY','1060',NULL,'EXPENSE',NULL,false,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:23:24.505','admin','2020-10-21 05:23:24.505',NULL,NULL,NULL,NULL)
,('0e47eeeb-1db5-4b45-9ae6-0bb794155b54','CONSIGNMENT','1070',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:23:33.143','admin','2020-10-21 05:23:33.143',NULL,NULL,NULL,NULL)
,('a8d79867-9367-4023-90c9-245f1389cb9a','OTHERS','1080',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:23:54.542','admin','2020-10-21 05:24:07.616',NULL,NULL,NULL,NULL)
,('99c00d31-f516-4292-a93f-6b5f4222a6a4','CLEARING ACCOUNT','1090',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:24:27.642','admin','2020-10-21 05:24:27.642',NULL,NULL,NULL,NULL)
,('8268386e-a585-4a15-bcca-5cd52cb644dd','BANK LOAN-BUILDING','1010',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:29:40.053','admin','2020-10-21 05:29:40.053',NULL,NULL,NULL,NULL)
,('1cea38a7-c814-4a68-ac5f-f3c90f002102','WORKING CAPITAL','1030',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:30:21.371','admin','2020-10-21 05:30:21.371',NULL,NULL,NULL,NULL)
,('a721c7ab-ad9a-4c39-8821-a9e1125ceea6','DUE TO FOUNDERS','1040',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:30:48.977','admin','2020-10-21 05:30:48.977',NULL,NULL,NULL,NULL)
,('634f0b40-45e2-41f5-bd3b-6325ce161f69','DUE TO SHAREHOLDERS','1050',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:40:57.481','admin','2020-10-21 05:40:57.481',NULL,NULL,NULL,NULL)
,('fe28acdc-ebb5-4ba8-a28e-e6ebb5ab7708','EWT DOCTORS FEE (5%)','1010',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:42:37.174','admin','2020-10-21 05:42:37.174',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('80388dee-d1d0-4fda-b49d-483804abca2a','WT DOCTORS FEE (10%)','1020',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:42:58.404','admin','2020-10-21 05:42:58.404',NULL,NULL,NULL,NULL)
,('35d7aa05-5a24-4ee1-aee1-923d1dc7b9b9','EWT DOCTORS FEE (15%)','1030',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:43:25.930','admin','2020-10-21 05:43:25.930',NULL,NULL,NULL,NULL)
,('a35d88a7-6109-473b-84ad-b0d9125b9cd7','EWT ON COMPENSATION','1040',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:44:00.932','admin','2020-10-21 05:44:00.932',NULL,NULL,NULL,NULL)
,('683e5906-242d-491e-9f74-dd13292e5f79','WT ON SUPPLIERS OF SERVICES (2%)','1050',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:44:18.507','admin','2020-10-21 05:44:18.507',NULL,NULL,NULL,NULL)
,('629c57ba-f71f-497c-84ab-ec04f7308ada','WITHHOLDING TAXES OTHERS','1060',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:44:33.687','admin','2020-10-21 05:44:33.687',NULL,NULL,NULL,NULL)
,('e6abbe30-52cd-4f75-a28e-c40c74817736','OUTPUT TAX','1070',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:45:15.158','admin','2020-10-21 05:45:15.158',NULL,NULL,NULL,NULL)
,('57130fec-366f-4466-8e60-458b4516a88b','VAT PAYABLE','1080',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:48:52.759','admin','2020-10-21 05:48:52.759',NULL,NULL,NULL,NULL)
,('efe2e48b-34e4-4965-aacf-76793fef709c','INCOME TAX PAYABLE','1090',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:49:07.689','admin','2020-10-21 05:49:07.689',NULL,NULL,NULL,NULL)
,('66f15bcb-1ea1-496d-bbfd-0ab7c9b4cd87','PROPERTY TAX PAYABLE-LAND','2000',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:49:45.869','admin','2020-10-21 05:49:45.869',NULL,NULL,NULL,NULL)
,('af6635e7-14df-46c6-a52b-c32a0c247ca8','PROPERTY TAX PAYABLE-BUILDING','2010',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:50:03.161','admin','2020-10-21 05:50:03.161',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('532961b3-4eed-49de-8920-8c64641e2b8d','PROPERTY TAX PAYABLE-EQUIPMENT','2020',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:50:20.727','admin','2020-10-21 05:50:20.727',NULL,NULL,NULL,NULL)
,('b6a22732-c050-43d5-a5a9-d27ff49395f0','PTP','1020',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:53:25.486','admin','2020-10-21 05:55:16.047',NULL,NULL,NULL,NULL)
,('99254d3c-4fe2-4951-b41e-44f4fb5fc34c','PCF/RF','1030',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:54:05.194','admin','2020-10-21 05:55:36.716',NULL,NULL,NULL,NULL)
,('a7039ffc-69b0-4a40-a470-30cd8e622afe','LEGAL FEES PAYABLE','1040',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:54:26.114','admin','2020-10-21 05:55:50.928',NULL,NULL,NULL,NULL)
,('b518db22-6365-47f6-88ed-de920a75f535','OTHER BIDDING PAYMENT','1050',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:57:30.764','admin','2020-10-21 05:57:30.764',NULL,NULL,NULL,NULL)
,('500a7139-9831-413e-82f2-50851da57e91','ACCRUED 13TH MONTH PAY','1060',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:57:54.515','admin','2020-10-21 05:57:54.515',NULL,NULL,NULL,NULL)
,('f81672e4-782b-47fc-8538-decf8898396b','ACCRUED AUDIT FEE','1070',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:58:11.765','admin','2020-10-21 05:58:11.765',NULL,NULL,NULL,NULL)
,('050650b3-98b2-4e24-83a2-0f7ac75dbb6e','ACCRUED PAYABLES','1080',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:58:36.016','admin','2020-10-21 05:58:36.016',NULL,NULL,NULL,NULL)
,('1ddade9b-b0db-4c90-818e-62446e5bd784','ADVANCES FROM PATIENT','1020','c1b6638e-141b-41fb-807d-cb4a8ce60c73','OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:59:31.617','admin','2020-10-21 05:59:40.274',NULL,NULL,NULL,NULL)
,('5e6783f1-ff44-44f0-9d56-65ca615b55ae','OTHER ADVANCES','1030','c1b6638e-141b-41fb-807d-cb4a8ce60c73','OTHERPAYMENTS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 06:12:24.073','admin','2020-10-21 06:12:24.073',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('125e9aac-35cc-4d84-923e-a54aea83aad3','OUTSTANDING CHECK','1090',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 06:17:16.277','admin','2020-10-21 06:17:16.277',NULL,NULL,NULL,NULL)
,('ed95eac7-dbb9-4337-a1a3-a734188fcc97','ADVANCES FROM EMPLOYEE','2000',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 06:17:36.950','admin','2020-10-21 06:17:36.950',NULL,NULL,NULL,NULL)
,('d1d686a9-f4bf-4b31-9576-8938bb0511c6','BANK LOAN-EQUIPMENT','1020',NULL,'EXPENSE',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 05:32:18.096','admin','2020-10-21 05:32:18.096',NULL,NULL,NULL,NULL)
,('df06d404-99e9-4240-af5f-6d1d4344035d','ROOM','1040',NULL,'REVENUEITEMS',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 09:11:50.435','admin','2020-10-21 09:11:50.435',NULL,NULL,NULL,NULL)
,('d5611022-5a7c-411e-959f-a3c83a047e07','DRUGS AND MEDICINE','1010',NULL,'REVENUEITEMS',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 08:58:58.608','admin','2020-10-21 09:00:02.668',NULL,NULL,NULL,NULL)
,('ebe616ae-d233-439d-8304-3513bf2c0a3a','MEDICAL SUPPLIES','1020',NULL,'REVENUEITEMS',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 09:03:05.337','admin','2020-10-21 09:03:05.337',NULL,NULL,NULL,NULL)
,('ef81a79b-e28d-42ca-8dc9-23bc274902de','DIAGNOSTICS AND SERVICES','1030',NULL,'REVENUEITEMS',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 09:10:42.347','admin','2020-10-21 09:10:42.347',NULL,NULL,NULL,NULL)
,('4674a57e-649f-4931-a629-9af17a0a1de4','OXYGEN','1050',NULL,'REVENUEITEMS',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 09:22:25.437','admin','2020-10-21 09:22:25.437',NULL,NULL,NULL,NULL)
,('94771e75-77dc-4fe4-b6c8-28fd6da464ce','VENTILATOR','1060',NULL,'REVENUEITEMS',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 09:38:49.036','admin','2020-10-21 09:39:28.244',NULL,NULL,NULL,NULL)
,('92dac718-657b-4412-86ee-83599beec4e3',' FND-DINING','1010',NULL,'REVENUEITEMS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 09:43:43.037','admin','2020-10-21 09:43:43.037',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('64c9edfe-5d3b-4b78-8064-6cffca8510d2','INTEREST INCOME-BFO','1020',NULL,'REVENUEITEMS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 09:45:19.405','admin','2020-10-21 09:45:19.405',NULL,NULL,NULL,NULL)
,('6fc5d136-0828-42b8-801d-bd6be2a35998','UNREALIZED FOREIGN EXCHANGE GAIN','1030',NULL,'REVENUEITEMS',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 09:46:00.203','admin','2020-10-21 09:46:00.203',NULL,NULL,NULL,NULL)
,('4091d976-ccc6-4606-8a65-fb2d5f0e144f','DISCOUNTS ','DISCOUNT',NULL,'OTHERENTITIES',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'com.hisd3.hismk2.domain.billing.Discount','admin','2020-10-21 23:06:08.323','admin','2020-10-21 23:06:08.323',NULL,NULL,NULL,NULL)
,('257ac906-c86c-4446-b23b-51b490454a2e','EMPLOYEES','1010',NULL,'EXPENSE',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 23:39:20.469','admin','2020-10-21 23:39:20.469',NULL,NULL,NULL,NULL)
,('ffd19f40-b6c6-4a0b-b630-0cbf49ba70fd','EMPLOYEES','1010',NULL,'EXPENSE',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 23:40:05.859','admin','2020-10-21 23:41:52.959',NULL,NULL,NULL,NULL)
,('dfdf9045-15d9-4d39-80c3-5fa7624c6261','EMPLOYEES','1010',NULL,'EXPENSE',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 23:43:04.070','admin','2020-10-21 23:43:04.070',NULL,NULL,NULL,NULL)
,('48d7e4eb-7a7b-4afc-bb26-e4b1a14a79ea','EMPLOYEES','1010',NULL,'EXPENSE',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 23:43:29.705','admin','2020-10-21 23:43:29.705',NULL,NULL,NULL,NULL)
,('5baa76dd-d631-413a-9d85-84ac070ef122','EMPLOYEES','1010',NULL,'EXPENSE',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-21 23:55:12.422','admin','2020-10-21 23:55:12.422',NULL,NULL,NULL,NULL)
,('b95230b8-21c2-491b-ba3a-f93a836d899b','EMPLOYEES','1010',NULL,'EXPENSE',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-22 01:36:44.394','admin','2020-10-22 01:36:44.394',NULL,NULL,NULL,NULL)
,('f52fab8e-89a9-4d47-9c05-207e9261b15c','EMPLOYEES','1010',NULL,'EXPENSE',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-22 01:37:20.694','admin','2020-10-22 01:37:20.694',NULL,NULL,NULL,NULL)
;
INSERT INTO accounting.subaccount_setup (id,description,subaccount_code,subaccount_parent,subaccount_type,include_department,attr_beginning_balance,attr_credit_memo_adj,attr_accrual_of_income,attr_non_trade_cash_receipts,attr_include_posting_accrued_income_multiple_customer,attr_vatable,attr_inactive,attr_expense_account,attr_debit_memo_adjustment,attr_accrual_expense,source_domain,created_by,created_date,last_modified_by,last_modified_date,journal_placement,category,require_remarks,attached_value) VALUES 
('a9c38a29-2191-4ad7-8d07-422c20490e34','EMPLOYEES','1010',NULL,'EXPENSE',true,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'admin','2020-10-22 01:37:44.073','admin','2020-10-22 01:37:44.073',NULL,NULL,NULL,NULL)
;

INSERT INTO "accounting"."subaccount_setup" ("id", "description", "subaccount_code", "subaccount_parent", "subaccount_type", "include_department", "attr_beginning_balance", "attr_credit_memo_adj", "attr_accrual_of_income", "attr_non_trade_cash_receipts", "attr_include_posting_accrued_income_multiple_customer", "attr_vatable", "attr_inactive", "attr_expense_account", "attr_debit_memo_adjustment", "attr_accrual_expense", "source_domain", "created_by", "created_date", "last_modified_by", "last_modified_date", "journal_placement", "category", "require_remarks", "attached_value") VALUES
('1c555d3d-6427-4a39-bc06-bf669c7ca08a', 'CLEARING', '1050', NULL, 'INCOME', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', '2020-12-05 03:14:53.149', 'admin', '2020-12-05 03:14:53.149', NULL, NULL, NULL, NULL);
INSERT INTO "accounting"."subaccount_mother_accounts" ("id", "sub_account", "chart_of_account", "created_by", "created_date", "last_modified_by", "last_modified_date") VALUES
('2b054df6-4c07-407d-a188-499d4e61f280', '1c555d3d-6427-4a39-bc06-bf669c7ca08a', '881eb9bc-b541-48e1-9f4c-bd80f4a2c8ac', 'admin', '2020-12-05 03:14:53.162', 'admin', '2020-12-05 03:14:53.162');