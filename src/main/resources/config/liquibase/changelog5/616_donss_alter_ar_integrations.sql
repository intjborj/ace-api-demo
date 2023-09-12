INSERT INTO accounting.integration (id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain", integration_group)
SELECT '4f8c307d-06da-4597-a06d-35d543ee9b15'::uuid, 'HMO CORPORATE AR INVOICE ', NULL, 'AR_CLAIMS_INVOICE', 2, 'admin', '2023-02-23 05:25:34.262', 'whinacay', '2023-03-21 10:42:57.102', NULL, 'com.hisd3.hismk2.domain.accounting.ArInvoice', 'd8144084-ddfe-4b2a-93fd-f35fe0298986'
WHERE NOT EXISTS (
        SELECT 1 FROM accounting.integration WHERE flag_value='AR_CLAIMS_INVOICE'
);

INSERT INTO accounting.integration (id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain", integration_group)
SELECT '43bf749f-82f4-43b7-98db-3e987cf1ca2f'::uuid, 'AR CREDIT NOTE ', NULL, 'AR_CREDIT_NOTE', 3, 'whinacay', '2023-03-19 04:49:55.969', 'whinacay', '2023-03-21 10:42:59.784', NULL, 'com.hisd3.hismk2.domain.accounting.ArCreditNote', 'd8144084-ddfe-4b2a-93fd-f35fe0298986'
WHERE NOT EXISTS (
        SELECT 1 FROM accounting.integration WHERE flag_value='AR_CREDIT_NOTE'
);


INSERT INTO accounting.integration (id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain", integration_group)
SELECT '85a861dc-3006-4b59-a14b-ac6e27d60a4b'::uuid, 'AR CREDIT NOTE TRANSFER', NULL, 'AR_CREDIT_NOTE_TRANSFER', 4, 'whinacay', '2023-03-19 10:03:20.270', 'whinacay', '2023-03-21 10:43:04.055', NULL, 'com.hisd3.hismk2.domain.accounting.ArCreditNote', 'd8144084-ddfe-4b2a-93fd-f35fe0298986'
WHERE NOT EXISTS (
        SELECT 1 FROM accounting.integration WHERE flag_value='AR_CREDIT_NOTE_TRANSFER'
);


-- INTEGRATION ITEMS

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '1f52c932-4662-44c4-96a3-621ac85a496d'::uuid, '{"code": "103-07000-0000", "subAccount": {"id": "20120234-8871-4c95-9e53-26c7a78f2e6e", "code": "07000", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "CLEARING ACCOUNT"}, "description": "ACCOUNTS RECEIVABLE-CLEARING ACCOUNT", "motherAccount": {"id": "9e12389d-3dbe-4dda-95e0-c09b1f36798c", "code": "103", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "ACCOUNTS RECEIVABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "CLEARING ACCOUNT", "subAccountSetupId": "20120234-8871-4c95-9e53-26c7a78f2e6e"}'::jsonb, NULL, NULL, NULL, '4f8c307d-06da-4597-a06d-35d543ee9b15'::uuid, 'admin', '2023-02-23 05:26:11.341', 'admin', '2023-02-23 05:35:56.936', NULL, 'negativeTotalHCIAmount', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='1f52c932-4662-44c4-96a3-621ac85a496d'::uuid
);


INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT 'ba787d99-703d-4bbb-8d0d-c1e7b2bf328c'::uuid, '{"code": "208-08000-0000", "subAccount": {"id": "b7eff7ab-daa7-48db-a661-6cae4a9b3f9c", "code": "08000", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "OUTPUT TAX"}, "description": "TAXES PAYABLE-OUTPUT TAX", "motherAccount": {"id": "4e5b3e7a-71df-4beb-9f0a-52347e8ccefd", "code": "208", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "TAXES PAYABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "OUTPUT TAX", "subAccountSetupId": "b7eff7ab-daa7-48db-a661-6cae4a9b3f9c"}'::jsonb, NULL, NULL, NULL, '4f8c307d-06da-4597-a06d-35d543ee9b15'::uuid, 'whinacay', '2023-03-17 10:32:18.004', 'whinacay', '2023-03-29 05:41:25.681', NULL, 'totalHCIVat', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='ba787d99-703d-4bbb-8d0d-c1e7b2bf328c'::uuid
);

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT 'be5d79bf-15ed-43dc-bee5-9355d2142757'::uuid, '{"code": "208-08000-0000", "subAccount": {"id": "b7eff7ab-daa7-48db-a661-6cae4a9b3f9c", "code": "08000", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "OUTPUT TAX"}, "description": "TAXES PAYABLE-OUTPUT TAX", "motherAccount": {"id": "4e5b3e7a-71df-4beb-9f0a-52347e8ccefd", "code": "208", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "TAXES PAYABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "OUTPUT TAX", "subAccountSetupId": "b7eff7ab-daa7-48db-a661-6cae4a9b3f9c"}'::jsonb, NULL, NULL, NULL, '43bf749f-82f4-43b7-98db-3e987cf1ca2f'::uuid, 'whinacay', '2023-03-19 04:50:56.381', 'whinacay', '2023-04-12 15:07:39.415', NULL, 'negativeVatAmount', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='be5d79bf-15ed-43dc-bee5-9355d2142757'::uuid
);

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT 'ce3c6d19-7957-4bf6-9f1a-52e910522463'::uuid, '{"code": "208-06000-0000", "subAccount": {"id": "19b35703-73a3-4365-9b3e-72a1cabd1688", "code": "06000", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "EWT ON SUPPLIERS OF SERVICES (2%)"}, "description": "TAXES PAYABLE-EWT ON SUPPLIERS OF SERVICES (2%)", "motherAccount": {"id": "f2b85d70-b144-4037-8f6c-cf734dab07f2", "code": "208", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "TAXES PAYABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "EWT ON SUPPLIERS OF SERVICES (2%)", "subAccountSetupId": "19b35703-73a3-4365-9b3e-72a1cabd1688"}'::jsonb, NULL, NULL, NULL, '4f8c307d-06da-4597-a06d-35d543ee9b15'::uuid, 'whinacay', '2023-03-17 10:32:43.743', 'whinacay', '2023-03-29 05:41:31.238', NULL, 'totalHCITax', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='ce3c6d19-7957-4bf6-9f1a-52e910522463'::uuid
);

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT 'dd1f15c5-e730-4290-adcc-39e73dce8ce8'::uuid, '{"code": "103-07000-0000", "subAccount": {"id": "20120234-8871-4c95-9e53-26c7a78f2e6e", "code": "07000", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "CLEARING ACCOUNT"}, "description": "ACCOUNTS RECEIVABLE-CLEARING ACCOUNT", "motherAccount": {"id": "9e12389d-3dbe-4dda-95e0-c09b1f36798c", "code": "103", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "ACCOUNTS RECEIVABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "CLEARING ACCOUNT", "subAccountSetupId": "20120234-8871-4c95-9e53-26c7a78f2e6e"}'::jsonb, NULL, NULL, NULL, '85a861dc-3006-4b59-a14b-ac6e27d60a4b'::uuid, 'whinacay', '2023-03-19 10:04:28.246', 'whinacay', '2023-03-19 10:04:36.297', NULL, 'totalAmountDue', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='dd1f15c5-e730-4290-adcc-39e73dce8ce8'::uuid
);

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '1b857413-808b-43ec-bcdd-9bf34c54652d'::uuid, '{"code": "103-####-0000", "subAccount": {"id": "03c9fd26-7fe5-49b6-a3c3-c3f72154e34a", "code": "####", "domain": "com.hisd3.hismk2.domain.billing.CompanyAccount", "normalSide": null, "description": "ACCOUNT RECEIVABLE GUARANTOR"}, "description": "ACCOUNTS RECEIVABLE-ACCOUNT RECEIVABLE GUARANTOR", "motherAccount": {"id": "5c03db72-fef0-4d44-ae19-4c21655db619", "code": "103", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "ACCOUNTS RECEIVABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "ACCOUNT RECEIVABLE GUARANTOR", "subAccountSetupId": "aa76ffb9-d5c8-420b-9a88-de5522435ffe"}'::jsonb, NULL, NULL, NULL, '4f8c307d-06da-4597-a06d-35d543ee9b15'::uuid, 'admin', '2023-02-25 06:53:11.911', 'whinacay', '2023-04-12 14:31:37.576', NULL, 'totalAmount', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='1b857413-808b-43ec-bcdd-9bf34c54652d'::uuid
);

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '810d2d49-50f8-4e0e-a039-827d028f55d9'::uuid, '{"code": "103-####-0000", "subAccount": {"id": "0496bf60-aada-4033-ab13-f76d09aa2235", "code": "####", "domain": "com.hisd3.hismk2.domain.billing.CompanyAccount", "normalSide": null, "description": "ACCOUNT RECEIVABLE GUARANTOR"}, "description": "ACCOUNTS RECEIVABLE-ACCOUNT RECEIVABLE GUARANTOR", "motherAccount": {"id": "5c03db72-fef0-4d44-ae19-4c21655db619", "code": "103", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "ACCOUNTS RECEIVABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "ACCOUNT RECEIVABLE GUARANTOR", "subAccountSetupId": "aa76ffb9-d5c8-420b-9a88-de5522435ffe"}'::jsonb, NULL, NULL, NULL, '43bf749f-82f4-43b7-98db-3e987cf1ca2f'::uuid, 'whinacay', '2023-03-19 04:50:20.811', 'whinacay', '2023-04-12 15:19:40.127', NULL, 'totalHCICreditNote', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='810d2d49-50f8-4e0e-a039-827d028f55d9'::uuid
);

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT 'ef22cedb-36a4-41fe-ad90-b59f72a1757e'::uuid, '{"code": "103-####-0000", "subAccount": {"id": "99283106-45a6-4f48-a640-9567af242398", "code": "####", "domain": "com.hisd3.hismk2.domain.billing.CompanyAccount", "normalSide": null, "description": "ACCOUNT RECEIVABLE GUARANTOR"}, "description": "ACCOUNTS RECEIVABLE-ACCOUNT RECEIVABLE GUARANTOR", "motherAccount": {"id": "5c03db72-fef0-4d44-ae19-4c21655db619", "code": "103", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "ACCOUNTS RECEIVABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "ACCOUNT RECEIVABLE GUARANTOR", "subAccountSetupId": "aa76ffb9-d5c8-420b-9a88-de5522435ffe"}'::jsonb, NULL, NULL, NULL, '85a861dc-3006-4b59-a14b-ac6e27d60a4b'::uuid, 'whinacay', '2023-03-19 10:03:29.802', 'whinacay', '2023-04-12 14:32:38.566', NULL, 'negativeTotalAmountDue', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='ef22cedb-36a4-41fe-ad90-b59f72a1757e'::uuid
);

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT 'dbf5c194-38d0-467e-902a-2fb6420e7611'::uuid, '{"code": "208-06000-0000", "subAccount": {"id": "19b35703-73a3-4365-9b3e-72a1cabd1688", "code": "06000", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "EWT ON SUPPLIERS OF SERVICES (2%)"}, "description": "TAXES PAYABLE-EWT ON SUPPLIERS OF SERVICES (2%)", "motherAccount": {"id": "f2b85d70-b144-4037-8f6c-cf734dab07f2", "code": "208", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "TAXES PAYABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "EWT ON SUPPLIERS OF SERVICES (2%)", "subAccountSetupId": "19b35703-73a3-4365-9b3e-72a1cabd1688"}'::jsonb, NULL, NULL, NULL, '43bf749f-82f4-43b7-98db-3e987cf1ca2f'::uuid, 'whinacay', '2023-03-19 04:51:06.326', 'whinacay', '2023-04-12 15:07:50.747', NULL, 'negativeCwtAmount', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='dbf5c194-38d0-467e-902a-2fb6420e7611'::uuid
);

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '576e8472-8a17-41b4-a02e-881ac7f6685d'::uuid, '{"code": "404-####-####", "subAccount": {"id": "cb469a9a-a295-4d60-a2f6-ac53843bc348", "code": "####", "domain": "com.hisd3.hismk2.domain.Department", "normalSide": null, "description": "Department"}, "description": "HOSPITAL DISCOUNTS AND ALLOWANCES-Department-DISCOUNT", "motherAccount": {"id": "af7e31b8-ec6a-45c0-8c26-74936da3766b", "code": "404", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "DEBIT", "description": "HOSPITAL DISCOUNTS AND ALLOWANCES"}, "subSubAccount": {"id": "ea8b37aa-a2ef-4f91-a78b-2786d4c71192", "code": "####", "domain": "com.hisd3.hismk2.domain.billing.Discount", "normalSide": null, "description": "DISCOUNT"}, "subAccountName": "DISCOUNT", "subAccountSetupId": "381a9e4c-b5e6-489a-95a1-6b6df9e6bbab"}'::jsonb, NULL, NULL, NULL, '43bf749f-82f4-43b7-98db-3e987cf1ca2f'::uuid, 'whinacay', '2023-03-19 04:51:14.283', 'whinacay', '2023-04-12 14:53:41.440', NULL, 'totalDiscount', true
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='2bb0eed6-563d-4af0-af9b-effd5895cbe4'::uuid
);

INSERT INTO accounting.integration_items (id, journal_account, disabled_property, disabled_value, value_property, integration, created_by, created_date, last_modified_by, last_modified_date, deleted, source_column, multiple)
SELECT '2bb0eed6-563d-4af0-af9b-effd5895cbe4'::uuid, '{"code": "201-02000-0000", "subAccount": {"id": "54892087-172a-4ec2-aebb-79a202588251", "code": "02000", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "DOCTOR''S FEE LIABILITY"}, "description": "ACCOUNTS PAYABLE-DOCTOR''S FEE LIABILITY", "motherAccount": {"id": "4cdf3b96-a180-48d2-8cd0-79ccea02ec12", "code": "201", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "ACCOUNTS PAYABLE"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "DOCTOR''S FEE LIABILITY", "subAccountSetupId": "54892087-172a-4ec2-aebb-79a202588251"}'::jsonb, NULL, NULL, NULL, '43bf749f-82f4-43b7-98db-3e987cf1ca2f'::uuid, 'whinacay', '2023-03-27 03:56:49.008', 'whinacay', '2023-04-12 15:19:49.585', NULL, 'totalPFCreditNote', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM accounting.integration_items WHERE id='2bb0eed6-563d-4af0-af9b-effd5895cbe4'::uuid
);

-- INTEGRATION DETAILS


INSERT INTO accounting.integration_items_details (id, field_name, field_value, integration_item)
SELECT 'b7066770-4005-4b80-8614-64e9b5fc6f37'::uuid, 'com.hisd3.hismk2.domain.billing.CompanyAccount', 'companyAccount', '1b857413-808b-43ec-bcdd-9bf34c54652d'
WHERE NOT EXISTS (
        SELECT 1 FROM accounting.integration_items_details WHERE id='b7066770-4005-4b80-8614-64e9b5fc6f37'::uuid
    );

INSERT INTO accounting.integration_items_details (id, field_name, field_value, integration_item)
SELECT '765ad9da-7976-4980-a726-b587fd5abfae'::uuid, 'com.hisd3.hismk2.domain.billing.CompanyAccount', 'companyAccount', 'ef22cedb-36a4-41fe-ad90-b59f72a1757e'
WHERE NOT EXISTS (
        SELECT 1 FROM accounting.integration_items_details WHERE id='765ad9da-7976-4980-a726-b587fd5abfae'::uuid
    );

INSERT INTO accounting.integration_items_details (id, field_name, field_value, integration_item)
SELECT '73f6bfb2-7c8b-4c42-8e2f-a770c0895e48'::uuid, 'com.hisd3.hismk2.domain.billing.Discount', 'discount', '576e8472-8a17-41b4-a02e-881ac7f6685d'
WHERE NOT EXISTS (
        SELECT 1 FROM accounting.integration_items_details WHERE id='73f6bfb2-7c8b-4c42-8e2f-a770c0895e48'::uuid
    );

INSERT INTO accounting.integration_items_details (id, field_name, field_value, integration_item)
SELECT '8562df49-2ff1-4fba-a3a4-5f3ebb48fc11'::uuid, 'com.hisd3.hismk2.domain.Department', 'department', '576e8472-8a17-41b4-a02e-881ac7f6685d'
WHERE NOT EXISTS (
        SELECT 1 FROM accounting.integration_items_details WHERE id='8562df49-2ff1-4fba-a3a4-5f3ebb48fc11'::uuid
    );

INSERT INTO accounting.integration_items_details (id, field_name, field_value, integration_item)
SELECT '79d089a7-c15e-4919-883c-ed50dc383f7f'::uuid, 'com.hisd3.hismk2.domain.billing.CompanyAccount', 'companyAccount', '810d2d49-50f8-4e0e-a039-827d028f55d9'
WHERE NOT EXISTS (
        SELECT 1 FROM accounting.integration_items_details WHERE id='79d089a7-c15e-4919-883c-ed50dc383f7f'::uuid
    );


