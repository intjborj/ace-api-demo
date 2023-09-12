-- Investor Subscription -------------------------------------------------------

INSERT INTO accounting.integration (id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, domain)
SELECT 'f4bfe1de-30fc-42e5-a27c-c4065514ede6', 'INVESTOR SUBSCRIPTION', NULL, 'INVESTOR_SUBSCRIPTION', 1000, NULL, '2022-02-21 13:28:29.94216', 'misadmin', '2022-02-21 05:33:38.624', NULL, 'com.hisd3.hismk2.domain.billing.Subscription'
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration WHERE id = 'f4bfe1de-30fc-42e5-a27c-c4065514ede6'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '66fccc93-589d-4974-96b5-02736f41105c', '{"code": "100120-100120-0000", "subAccount": {"id": "013b84e4-3c64-446b-852d-81c441d3f112", "code": "100120", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "SUBSCRIPTION RECEIVABLE"}, "description": "SUBSCRIPTION RECEIVABLE-SUBSCRIPTION RECEIVABLE", "motherAccount": {"id": "4bf51ff3-81a4-4c43-9d07-f53b8152c351", "code": "100120", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "SUBSCRIPTION RECEIVABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "SUBSCRIPTION RECEIVABLE", "subAccountSetupId": "013b84e4-3c64-446b-852d-81c441d3f112"}', NULL, NULL, NULL, 'f4bfe1de-30fc-42e5-a27c-c4065514ede6', 'misadmin', '2022-02-21 05:31:13.587', 'misadmin', '2022-02-21 05:32:28.974', NULL, 'subscriptionReceivable', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '66fccc93-589d-4974-96b5-02736f41105c'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '0eecca02-0b26-47fd-84a7-39da0c071d20', '{"code": "600700-600700-0000", "subAccount": {"id": "1e6bc554-d492-4718-80df-7f85addd2bff", "code": "600700", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "SUBSCRIBED SHARE CAPITAL"}, "description": "SUBSCRIBED SHARE CAPITAL-SUBSCRIBED SHARE CAPITAL", "motherAccount": {"id": "daf5e6af-660c-4743-8f77-d8dfd76fda4b", "code": "600700", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "SUBSCRIBED SHARE CAPITAL"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "SUBSCRIBED SHARE CAPITAL", "subAccountSetupId": "1e6bc554-d492-4718-80df-7f85addd2bff"}', NULL, NULL, NULL, 'f4bfe1de-30fc-42e5-a27c-c4065514ede6', 'misadmin', '2022-02-21 05:31:47.549', 'misadmin', '2022-02-21 05:32:35.307', NULL, 'subscribedShareCapital', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '0eecca02-0b26-47fd-84a7-39da0c071d20'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT 'fc92610b-8561-491a-82ec-2e7e7e92c01a', '{"code": "300020-300020-0000", "subAccount": {"id": "76971008-e658-4e6e-be1d-8a6e13bc47c9", "code": "300020", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "ADDITIONAL PAID IN CAPITAL"}, "description": "ADDITIONAL PAID IN CAPITAL-ADDITIONAL PAID IN CAPITAL", "motherAccount": {"id": "332b710c-2050-41dd-8ea4-569e658873f0", "code": "300020", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "ADDITIONAL PAID IN CAPITAL"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "ADDITIONAL PAID IN CAPITAL", "subAccountSetupId": "76971008-e658-4e6e-be1d-8a6e13bc47c9"}', NULL, NULL, NULL, 'f4bfe1de-30fc-42e5-a27c-c4065514ede6', 'misadmin', '2022-02-21 05:32:04.873', 'misadmin', '2022-02-21 05:32:39.113', NULL, 'additionalPaidInCapital', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = 'fc92610b-8561-491a-82ec-2e7e7e92c01a'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '7f6780a7-8df0-4997-aafe-87d5560cbfe9', '{"code": "600800-600800-0000", "subAccount": {"id": "8f8231c9-cdc0-4546-bbfd-25938e31f331", "code": "600800", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "DISCOUNT ON SHARE CAPITAL"}, "description": "DISCOUNT ON SHARE CAPITAL-DISCOUNT ON SHARE CAPITAL", "motherAccount": {"id": "8a1613e9-7225-4497-894d-41cfd1ac2007", "code": "600800", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "DISCOUNT ON SHARE CAPITAL"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "DISCOUNT ON SHARE CAPITAL", "subAccountSetupId": "8f8231c9-cdc0-4546-bbfd-25938e31f331"}', NULL, NULL, NULL, 'f4bfe1de-30fc-42e5-a27c-c4065514ede6', 'misadmin', '2022-02-21 05:32:12.898', 'misadmin', '2022-02-21 05:32:43.541', NULL, 'discountOnShareCapital', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '7f6780a7-8df0-4997-aafe-87d5560cbfe9'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT 'b1876474-5ae3-4e99-9357-cd15a1f6261c', '{"code": "600900-600900-0000", "subAccount": {"id": "08c5b064-6849-47e7-9b8e-385c4e9d283c", "code": "600900", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "ADVANCES FROM INVESTORS"}, "description": "ADVANCES FROM INVESTORS-ADVANCES FROM INVESTORS", "motherAccount": {"id": "34c33d58-032e-4958-9426-3f1b3847dd30", "code": "600900", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "ADVANCES FROM INVESTORS"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "ADVANCES FROM INVESTORS", "subAccountSetupId": "08c5b064-6849-47e7-9b8e-385c4e9d283c"}', NULL, NULL, NULL, 'f4bfe1de-30fc-42e5-a27c-c4065514ede6', 'misadmin', '2022-02-21 05:32:22.031', 'misadmin', '2022-02-21 05:32:52.893', NULL, 'advancesFromInvestors', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = 'b1876474-5ae3-4e99-9357-cd15a1f6261c'
  );

-- Investor Payment -------------------------------------------------------

INSERT INTO accounting.integration (id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, domain)
SELECT '6f875976-5595-4be2-8a90-072b5ad369d7', 'INVESTOR PAYMENTS', NULL, 'INVESTOR_PAYMENT', 1000, NULL, '2022-02-21 11:19:52.584474', 'misadmin', '2022-02-21 05:23:15.193', NULL, 'com.hisd3.hismk2.domain.cashiering.PaymentTracker'
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration WHERE id = '6f875976-5595-4be2-8a90-072b5ad369d7'
  );


INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '8187c016-f49d-4ec2-bc92-b263f9775482', '{"code": "100010-####-0000", "subAccount": {"id": "5ecb5bbd-03e5-4022-aa43-10c7523081e0", "code": "####", "domain": "com.hisd3.hismk2.domain.cashiering.CashierTerminal", "normalSide": null, "description": "CASH ON HAND CASHER TERMINAL ASSIGN."}, "description": "CASH ON HAND-CASH ON HAND CASHER TERMINAL ASSIGN.", "motherAccount": {"id": "90caf006-9a05-4f78-aec8-d6f38916c9f2", "code": "100010", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "CASH ON HAND"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "CASH ON HAND CASHER TERMINAL ASSIGN.", "subAccountSetupId": "9894f3ce-c956-4456-8bfb-d8e92db2a31d"}', NULL, NULL, NULL, '6f875976-5595-4be2-8a90-072b5ad369d7', 'misadmin', '2022-02-21 03:22:57.527', 'misadmin', '2022-02-21 03:25:39.579', NULL, 'totalCash', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '8187c016-f49d-4ec2-bc92-b263f9775482'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '04bcf2ba-237c-413b-92ce-40f9599ce508', '{"code": "100020-####-0000", "subAccount": {"id": "6682e176-e724-49bd-aa4d-617ea7cd3267", "code": "####", "domain": "com.hisd3.hismk2.domain.cashiering.CashierTerminal", "normalSide": null, "description": "CHECK ON HAND CASHIER TERMINALS ASSGN."}, "description": "CHECK ON HAND-CHECK ON HAND CASHIER TERMINALS ASSGN.", "motherAccount": {"id": "17d8f2a1-71d5-406e-b148-2e13d958ec05", "code": "100020", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "CHECK ON HAND"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "CHECK ON HAND CASHIER TERMINALS ASSGN.", "subAccountSetupId": "ca08d089-c633-4a13-8967-2e40d72a9b80"}', NULL, NULL, NULL, '6f875976-5595-4be2-8a90-072b5ad369d7', 'misadmin', '2022-02-21 03:23:16.297', 'misadmin', '2022-02-21 03:25:47.749', NULL, 'totalCheck', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '04bcf2ba-237c-413b-92ce-40f9599ce508'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '04bcf2ba-237c-413b-92ce-40f9599ce508', '{"code": "100020-####-0000", "subAccount": {"id": "6682e176-e724-49bd-aa4d-617ea7cd3267", "code": "####", "domain": "com.hisd3.hismk2.domain.cashiering.CashierTerminal", "normalSide": null, "description": "CHECK ON HAND CASHIER TERMINALS ASSGN."}, "description": "CHECK ON HAND-CHECK ON HAND CASHIER TERMINALS ASSGN.", "motherAccount": {"id": "17d8f2a1-71d5-406e-b148-2e13d958ec05", "code": "100020", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "CHECK ON HAND"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "CHECK ON HAND CASHIER TERMINALS ASSGN.", "subAccountSetupId": "ca08d089-c633-4a13-8967-2e40d72a9b80"}', NULL, NULL, NULL, '6f875976-5595-4be2-8a90-072b5ad369d7', 'misadmin', '2022-02-21 03:23:16.297', 'misadmin', '2022-02-21 03:25:47.749', NULL, 'totalCheck', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '04bcf2ba-237c-413b-92ce-40f9599ce508'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '48f25bd7-ba7b-4fe1-8b37-f01d6bd17976', '{"code": "100060-####-0000", "subAccount": {"id": "ec77e546-3cc4-40a0-859c-751a2285351a", "code": "####", "domain": "com.hisd3.hismk2.domain.accounting.Bank", "normalSide": null, "description": "CASH IN BANK SETUP"}, "description": "CASH IN BANK-CASH IN BANK SETUP", "motherAccount": {"id": "3e2ecf16-dae5-4bbb-bf7e-ffe8efb42ac8", "code": "100060", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "CASH IN BANK"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "CASH IN BANK SETUP", "subAccountSetupId": "f78f76c8-4d70-4755-9d66-1579eb1d00b5"}', NULL, NULL, NULL, '6f875976-5595-4be2-8a90-072b5ad369d7', 'misadmin', '2022-02-21 03:24:08.267', 'misadmin', '2022-02-21 03:26:42.745', NULL, 'amountForCashDeposit', 't'
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '48f25bd7-ba7b-4fe1-8b37-f01d6bd17976'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '8fe65b0d-1081-44ee-a3e5-3cec9c4205c2', '{"code": "100090-1020-####", "subAccount": {"id": "20b5b8b6-d279-4578-870f-ebb3f3d6e17e", "code": "1020", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "CREDIT CARD"}, "description": "ACCOUNTS RECEIVABLES-CREDIT CARD-CREDIT CARD PAYMENTS AR SETUP", "motherAccount": {"id": "8695271b-aeb3-4df8-9d34-2e7d973f9e25", "code": "100090", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "ACCOUNTS RECEIVABLES"}, "subSubAccount": {"id": "fa350b85-5718-425e-841c-ee605fe3d19a", "code": "####", "domain": "com.hisd3.hismk2.domain.accounting.Bank", "normalSide": null, "description": "CREDIT CARD PAYMENTS AR SETUP"}, "subAccountName": "CREDIT CARD PAYMENTS AR SETUP", "subAccountSetupId": "2d056ddb-c2d0-411b-ad0c-30d753c0d557"}', NULL, NULL, NULL, '6f875976-5595-4be2-8a90-072b5ad369d7', 'misadmin', '2022-02-21 03:23:38.228', 'misadmin', '2022-02-21 03:26:41.193', NULL, 'amountForCreditCard', 't'
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '8fe65b0d-1081-44ee-a3e5-3cec9c4205c2'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT 'd9cf5453-12fc-42f7-8998-b970f85e0234', '{"code": "100120-100120-0000", "subAccount": {"id": "013b84e4-3c64-446b-852d-81c441d3f112", "code": "100120", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "SUBSCRIPTION RECEIVABLE"}, "description": "SUBSCRIPTION RECEIVABLE-SUBSCRIPTION RECEIVABLE", "motherAccount": {"id": "4bf51ff3-81a4-4c43-9d07-f53b8152c351", "code": "100120", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "SUBSCRIPTION RECEIVABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "SUBSCRIPTION RECEIVABLE", "subAccountSetupId": "013b84e4-3c64-446b-852d-81c441d3f112"}', NULL, NULL, NULL, '6f875976-5595-4be2-8a90-072b5ad369d7', 'misadmin', '2022-02-21 03:24:20.185', 'misadmin', '2022-02-21 03:26:18.233', NULL, 'subscriptionReceivable', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = 'd9cf5453-12fc-42f7-8998-b970f85e0234'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '53eb4c9e-74ef-4347-ae4b-01845c202434', '{"code": "300010-300010-0000", "subAccount": {"id": "4368f9de-e139-4adf-b727-ebc98b5b1685", "code": "300010", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "SHARE CAPITAL"}, "description": "SHARE CAPITAL-SHARE CAPITAL", "motherAccount": {"id": "dbf060c1-c67a-434e-8670-e2099f800f85", "code": "300010", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "SHARE CAPITAL"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "SHARE CAPITAL", "subAccountSetupId": "4368f9de-e139-4adf-b727-ebc98b5b1685"}', NULL, NULL, NULL, '6f875976-5595-4be2-8a90-072b5ad369d7', 'misadmin', '2022-02-21 03:24:31.238', 'misadmin', '2022-02-21 03:26:23.979', NULL, 'shareCapital', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '53eb4c9e-74ef-4347-ae4b-01845c202434'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '3b8aacd6-cae3-45eb-8eeb-a1391d3d943c', '{"code": "600700-600700-0000", "subAccount": {"id": "1e6bc554-d492-4718-80df-7f85addd2bff", "code": "600700", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "SUBSCRIBED SHARE CAPITAL"}, "description": "SUBSCRIBED SHARE CAPITAL-SUBSCRIBED SHARE CAPITAL", "motherAccount": {"id": "daf5e6af-660c-4743-8f77-d8dfd76fda4b", "code": "600700", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "SUBSCRIBED SHARE CAPITAL"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "SUBSCRIBED SHARE CAPITAL", "subAccountSetupId": "1e6bc554-d492-4718-80df-7f85addd2bff"}', NULL, NULL, NULL, '6f875976-5595-4be2-8a90-072b5ad369d7', 'misadmin', '2022-02-21 03:24:47.777', 'misadmin', '2022-02-21 03:26:32.242', NULL, 'subscribedShareCapital', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '3b8aacd6-cae3-45eb-8eeb-a1391d3d943c'
  );

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '180f3d73-7299-451c-a4f3-a4a978be51c8', '{"code": "600900-600900-0000", "subAccount": {"id": "08c5b064-6849-47e7-9b8e-385c4e9d283c", "code": "600900", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "ADVANCES FROM INVESTORS"}, "description": "ADVANCES FROM INVESTORS-ADVANCES FROM INVESTORS", "motherAccount": {"id": "34c33d58-032e-4958-9426-3f1b3847dd30", "code": "600900", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "ADVANCES FROM INVESTORS"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "ADVANCES FROM INVESTORS", "subAccountSetupId": "08c5b064-6849-47e7-9b8e-385c4e9d283c"}', NULL, NULL, NULL, '6f875976-5595-4be2-8a90-072b5ad369d7', 'misadmin', '2022-02-21 03:25:12.628', 'misadmin', '2022-02-21 03:26:36.325', NULL, 'advancesFromInvestors', NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '180f3d73-7299-451c-a4f3-a4a978be51c8'
  );

INSERT INTO accounting.integration_items_details (id, field_name, field_value, integration_item)
SELECT '1d3f509a-60d8-49ac-8559-9f4fc53f8788', 'com.hisd3.hismk2.domain.cashiering.CashierTerminal', 'cashierTerminal', '8187c016-f49d-4ec2-bc92-b263f9775482'
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items_details WHERE id = '1d3f509a-60d8-49ac-8559-9f4fc53f8788'
  );

INSERT INTO accounting.integration_items_details (id, field_name, field_value, integration_item)
SELECT '38190d50-5664-4e9b-851e-e0690df94589', 'com.hisd3.hismk2.domain.cashiering.CashierTerminal', 'cashierTerminal', '04bcf2ba-237c-413b-92ce-40f9599ce508'
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items_details WHERE id = '38190d50-5664-4e9b-851e-e0690df94589'
  );

INSERT INTO accounting.integration_items_details (id, field_name, field_value, integration_item)
SELECT '024cbeef-73a7-4c21-a418-e0af01646852', 'com.hisd3.hismk2.domain.accounting.Bank', 'bankForCreditCard', '8fe65b0d-1081-44ee-a3e5-3cec9c4205c2'
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items_details WHERE id = '024cbeef-73a7-4c21-a418-e0af01646852'
  );

INSERT INTO accounting.integration_items_details (id, field_name, field_value, integration_item)
SELECT 'c6b92ed6-33d0-4a26-b797-be56d764ea25', 'com.hisd3.hismk2.domain.accounting.Bank', 'bankForCashDeposit', '48f25bd7-ba7b-4fe1-8b37-f01d6bd17976'
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items_details WHERE id = 'c6b92ed6-33d0-4a26-b797-be56d764ea25'
  );
