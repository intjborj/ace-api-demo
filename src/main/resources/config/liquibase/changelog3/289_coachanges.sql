delete from accounting.account_list ;
delete from accounting.chart_of_accounts ;

ALTER TABLE accounting.chart_of_accounts ALTER COLUMN id SET DEFAULT uuid_generate_v4();

INSERT INTO accounting.chart_of_accounts (id,account_code,description,category,tags,parent,deprecated,created_by,created_date,last_modified_by,last_modified_date,deleted,account_type,fs_type,normal_side,is_contra) VALUES
('f4e22dd6-20f1-4f1e-8c24-ca916a584ba9','100010','CASH ON HAND',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('eba97f9a-d12c-46e3-a1c7-8f9b32852a32','100020','CASH IN BANK',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('c385c560-7e9e-46cf-88f8-3acd141427ea','100030','PETTY CASH FUND',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('986951af-ec8d-41ed-9abf-5278da2df936','100040','ACCOUNTS RECEIVABLES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('0cc986c5-4e83-488f-8197-9c4be36b2aa9','100050','ALLOWANCE FOR DOUBTFUL ACCOUNTS',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','CREDIT',true)
                                                                                                                                                                                                                           ,('00f8efd4-12d4-4ddb-92f0-f38861e8335b','100060','ADVANCES TO EMPLOYEES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('22e463b6-c0a4-4576-844a-d1cd3224cb20','100070','ADVANCES TO SUPPLIERS',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('6a25ea02-74d8-4943-9fde-cbdc5deceb33','100080','OTHER RECEIVABLES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('c8149b54-11ad-4b96-96f2-6377c55efeea','100090','INVENTORY',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('81a183e4-4123-4f0d-87e7-23f3bab1c1c8','100100','CREDITABLE WITHHOLDING TAX',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
;
INSERT INTO accounting.chart_of_accounts (id,account_code,description,category,tags,parent,deprecated,created_by,created_date,last_modified_by,last_modified_date,deleted,account_type,fs_type,normal_side,is_contra) VALUES
('b0cd60bc-c4d2-4794-969c-1f55b6fa7838','100110','PROPERTY, PLANT AND EQUIPMENT',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('653ce7fe-133a-41cf-9b1b-ff3274f750f5','100120','ACCUMULATED DEPRECIATION',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','CREDIT',true)
                                                                                                                                                                                                                           ,('e381864b-0ee8-4fd3-baf5-d5a81bf8019a','100130','INPUT TAX',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('aacb3008-d739-46c1-b220-158691c57d94','100140','CASH OVER/SHORT',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'ASSET','BALANCE_SHEET','DEBIT',false)
                                                                                                                                                                                                                           ,('922260ec-c0a1-46a6-bdad-d055cfec6f7d','200010','DUE TO DOCTORS',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('2300bee8-8507-4910-a56d-5eb4af929099','200020','ACCOUNTS PAYABLE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('2cbc0234-fbb7-4b9f-b094-eb77672fa536','200030','PATIENTS REFUND PAYABLE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('c3b1611e-757f-4690-a664-9aaf93ec3424','200040','EXPANDED WITHHOLDING TAX PAYABLE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('ef50f3ce-33c0-4e47-8c78-aff81c18ee05','200050','SSS PREMIUM PAYABLE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('7a24c840-7995-4ef8-866d-6097bd5d927b','200060','PHILHEALTH PREMIUM PAYABLE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
;
INSERT INTO accounting.chart_of_accounts (id,account_code,description,category,tags,parent,deprecated,created_by,created_date,last_modified_by,last_modified_date,deleted,account_type,fs_type,normal_side,is_contra) VALUES
('6014d358-73df-469d-b2d8-ca1a60bfe65d','200070','PAG-IBIG PREMIUM PAYABLE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('a24b3a56-196d-43ca-9c9f-aabe2d28b6e0','200080','SSS SALARY LOAN PAYABLE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('54b79410-3b0e-4289-b8ca-fcca0764d897','200090','PAG-IBIG LOAN PAYABLE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('3a9a872b-ca24-4e86-bcd4-46537e39479f','200100','AP CLEARING',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('fc1161c9-be0f-42df-8988-4144e698bfd5','200110','PF CLEARING',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('ff505d7d-7ef1-494f-9976-d4c12f250678','200120','INCOME TAX PAYABLE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('6a4db614-f132-4510-b893-e865d1c8eca9','200130','LOANS PAYABLE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('09345235-4999-4b29-b283-ec6a2bae3cf6','200140','OUTPUT TAX',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'LIABILITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('93c0e7f3-1cc0-4ad6-a648-75ce2f0a8daa','300010','STOCKHOLDERS EQUITY',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EQUITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('f2bad4a4-868b-4942-b8f3-2b6f20d5d230','300020','RETAINED EARNINGS',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EQUITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('ce0cd612-72ed-4d41-b02f-e0c9a06ce6fd','300030','DIVIDENDS',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EQUITY','BALANCE_SHEET','DEBIT',true);
INSERT INTO accounting.chart_of_accounts (id,account_code,description,category,tags,parent,deprecated,created_by,created_date,last_modified_by,last_modified_date,deleted,account_type,fs_type,normal_side,is_contra) VALUES
('ec66b031-11d7-4770-bbd9-345f6181833a','300030','NET INCOME(LOSS)',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EQUITY','BALANCE_SHEET','CREDIT',false)
                                                                                                                                                                                                                           ,('f69583ec-75c3-425f-8d14-f12f2d04da75','400010','SALES REVENUE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'REVENUE','INCOME','CREDIT',false)
                                                                                                                                                                                                                           ,('e9a9bad5-49cb-4f00-8144-a86a1826f83e','400020','SERVICE REVENUE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'REVENUE','INCOME','CREDIT',false)
                                                                                                                                                                                                                           ,('305d72d4-4b53-49fb-93d8-6627efdea112','400030','ROOM AND BOARD',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'REVENUE','INCOME','CREDIT',false)
                                                                                                                                                                                                                           ,('00d4e994-1852-46ed-ae99-d952a7cbe780','400040','OTHER INCOME',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'REVENUE','INCOME','CREDIT',false)
                                                                                                                                                                                                                           ,('3ca6855b-5b43-4eb7-b4fe-bce317ab58df','500010','COST OF SALES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('f36977d7-52e5-4d03-a810-9f7d582b8d89','500020','EMPLOYEE DISCOUNT',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('5ee961a0-4fcf-41dd-bd92-8cbda400cb3b','500030','SENIOR CITIZEN DISCOUNT',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('d92ce61d-832f-4508-ab34-4b7bc9626d05','500040','HOSPITAL DISCOUNT',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('555821e1-8e3f-42db-8688-83a3977a57c8','500050','PWD DISCOUNT',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
;
INSERT INTO accounting.chart_of_accounts (id,account_code,description,category,tags,parent,deprecated,created_by,created_date,last_modified_by,last_modified_date,deleted,account_type,fs_type,normal_side,is_contra) VALUES
('c6bd6a7f-2f94-49db-9c8b-6e6525a69e72','500060','PERSONAL DISCOUNT',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('4f99ea9d-9c08-494d-a2ba-95c0df43403a','500070','SALARIES AND WAGES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('ea1df8cf-aa57-471d-994e-81f2d7267328','500080','STAFF DEVELOPMENT AND TRAININGS',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('47a98cab-114d-4b1f-b7db-81e5b13a9d94','500090','PAG-IBIG PREMIUM EXPENSES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('f51b480c-7a96-4a49-b3be-0ac380296e9c','500100','PHILHEALTH PREMIUM EXPENSES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('59726616-bb3d-499e-a917-131e99df32df','500110','SSS PREMIUM EXPENSES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('732b506b-e830-44f7-a703-346cd6966244','500120','DEPRECIATION EXPENSE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('deab0bea-d4df-494d-868a-5bc271c68740','500130','HOSPITAL HOUSEKEEPING AND JANITORIAL SUPPLIES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('8e6c3aa6-44e5-4a29-a8e6-249c84c6f44f','500140','MEDICAL, LABORATORY AND HOSPITAL SUPPLIES USED',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('3d3bcc25-4652-4aa7-b5e3-ba70a6280ffa','500150','CONTRACTED SERVICES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
;
INSERT INTO accounting.chart_of_accounts (id,account_code,description,category,tags,parent,deprecated,created_by,created_date,last_modified_by,last_modified_date,deleted,account_type,fs_type,normal_side,is_contra) VALUES
('5952e2a1-f20e-4f95-a944-0aece87f9ddc','500160','RESIDENT DOCTOR ON DUTY',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('b114030e-6c1a-4d4f-a381-cc2711f2097b','500170','DIETARY AND MARKETING EXPENSE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('75ce0d70-333e-4377-8cd7-037270f704ae','500180','INTEREST EXPENSE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('96ac0ea9-1e42-4b4a-b4c7-efa844374ced','500190','LIGHT AND WATER',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('0c250d34-b745-4333-a7e4-fdcd364613bd','500200','PRE-OPERATING EXPENSE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('2d6de098-3be2-4a69-b661-db4c5544eeef','500210','SECURITY EXPENSE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('3f4e5ae2-d1dd-4adc-9b26-98be8f9a27c8','500220','TAXES-PERMITS, REGISTRATION AND LICENSES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('7e5ae45d-7335-472f-ae45-eef2a649ebc6','500230','OFFICE SUPPLIES EXPENSE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('04c8023e-d81c-4c38-9d04-c97586d1d83a','500240','FUEL EXPENSE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('49f99ff3-2398-4af2-b45d-b524ca09e1be','500250','REPRESENTATION EXPENSE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
;
INSERT INTO accounting.chart_of_accounts (id,account_code,description,category,tags,parent,deprecated,created_by,created_date,last_modified_by,last_modified_date,deleted,account_type,fs_type,normal_side,is_contra) VALUES
('ae7fb085-2d31-494b-a205-9fc0465a9c4f','500260','TELEPHONE AND COMMUNICATION',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('0e8d0c31-3605-4c70-aa7b-a6c35e995187','500270','TRANSPORTATION AND TRAVELLING EXPENSES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('4b3d01ea-efe5-4147-8109-cd2211d46c82','500280','INSURANCES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('39edb9d2-21fa-4084-9e00-f03f4be33ea1','500290','RETAINER FEE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('6640483d-1959-4029-9273-9fb9a915e138','500300','MEMBERSHIP AND DUES FEE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('af81ef80-9867-4feb-8135-f4af0841a1b4','500310','MISCELLANEOUS EXPENSE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('174b7a24-1028-4411-bf8a-efe0d56fa6c5','500320','BANK CHARGES',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('2694ea4e-6216-4e52-8425-a01f18246a50','500330','AUDIT FEE',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
                                                                                                                                                                                                                           ,('f25d60ee-1b6e-4ccf-b7c1-b7a5143bd85e','500340','MAINTENANCE AND REPAIR',NULL,NULL,NULL,NULL,NULL,'2020-09-16 17:06:52.333',NULL,'2020-09-16 17:06:52.333',NULL,'EXPENSE','INCOME','DEBIT',false)
;