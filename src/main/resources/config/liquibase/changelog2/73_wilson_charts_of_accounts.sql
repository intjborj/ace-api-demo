DELETE FROM accounting.chart_of_accounts
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('2a0d7e40-c25a-4e1c-833e-9535e3a6da0b', '01', 'ASSETS', true, 'ASSETS', NULL, false, 'system', '2020-03-12 14:01:58.540', 'system', '2020-03-12 14:01:58.540', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', '01', 'CURRENT ASSETS', true, 'FS_FINPOS_CURRENT_ASSETS', '2a0d7e40-c25a-4e1c-833e-9535e3a6da0b', false, 'system', '2020-03-12 14:01:58.631', 'system', '2020-03-12 14:01:58.631', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('6ebcd4fb-1635-4bbb-a689-7650ca083162', '01', 'CASH AND EQUIVALENTS', true, '', '9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', false, 'system', '2020-03-12 14:01:58.643', 'system', '2020-03-12 14:01:58.643', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('1ba60822-a62d-4eef-80f5-b50fce21d2df', '001', 'CASH ON HAND', false, 'CASHIER_TERMINALS,CASH_ON_HAND', '6ebcd4fb-1635-4bbb-a689-7650ca083162', false, 'system', '2020-03-12 14:01:58.653', 'system', '2020-03-12 14:01:58.653', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('b47a7471-2c1d-4d7f-aab7-d9ac9c8644ff', '002', 'CHECK ON HAND', false, 'CASHIER_TERMINALS,CHECK_ON_HAND', '6ebcd4fb-1635-4bbb-a689-7650ca083162', false, 'system', '2020-03-12 14:01:58.678', 'system', '2020-03-12 14:01:58.678', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('b8e14f0b-f7e4-4730-aca4-49cd65fcfe99', '003', 'CASH IN BANK', false, 'BANK_ACCOUNTS,CASH_IN_BANK', '6ebcd4fb-1635-4bbb-a689-7650ca083162', false, 'system', '2020-03-12 14:01:58.686', 'system', '2020-03-12 14:01:58.686', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('855c3426-50f4-425c-8304-bea2b478cb84', '004', 'PETTY CASH FUND', false, '', '6ebcd4fb-1635-4bbb-a689-7650ca083162', false, 'system', '2020-03-12 14:01:58.690', 'system', '2020-03-12 14:01:58.690', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('21f0ffaa-8764-462d-b586-cd4bb5719930', '005', 'REVOLVING FUND', false, '', '6ebcd4fb-1635-4bbb-a689-7650ca083162', false, 'system', '2020-03-12 14:01:58.695', 'system', '2020-03-12 14:01:58.695', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('55baa272-95db-4ae0-a6e3-aeaadc6b4414', '006', 'CHANGE FUND', false, '', '6ebcd4fb-1635-4bbb-a689-7650ca083162', false, 'system', '2020-03-12 14:01:58.700', 'system', '2020-03-12 14:01:58.700', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('097d337e-8ebd-4e70-8699-6e889c6ca60d', '02', 'INVESTMENTS', true, '', '9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', false, 'system', '2020-03-12 14:01:58.705', 'system', '2020-03-12 14:01:58.705', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('0fb982f1-bbaf-4f51-98ed-45524921dfa5', '001', 'SHORT-TERM INVESTMENTS', false, '', '097d337e-8ebd-4e70-8699-6e889c6ca60d', false, 'system', '2020-03-12 14:01:58.710', 'system', '2020-03-12 14:01:58.710', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('634684d9-ee40-44e0-a77f-933f33cc18bd', '002', 'OTHER CURRENT INVESTMENTS', false, '', '097d337e-8ebd-4e70-8699-6e889c6ca60d', false, 'system', '2020-03-12 14:01:58.718', 'system', '2020-03-12 14:01:58.718', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('2db90cd8-bdb7-4e84-84ad-52dc8664b19e', '03', 'ACCOUNTS RECEIVABLES', true, '', '9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', false, 'system', '2020-03-12 14:01:58.733', 'system', '2020-03-12 14:01:58.733', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('75704010-474b-43b6-82b4-0b95331b76c1', '001', 'INPATIENT INDIVIDUAL ACCOUNT RECEIVABLES', false, 'AR_DEBIT_INPATIENT', '2db90cd8-bdb7-4e84-84ad-52dc8664b19e', false, 'system', '2020-03-12 14:01:58.738', 'system', '2020-03-12 14:01:58.738', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('cec9edef-7e41-408b-9cbc-f538110b2bbe', '002', 'OUTPATIENT INDIVIDUAL ACCOUNT RECEIVABLES', false, 'AR_DEBIT_OUTPATIENT', '2db90cd8-bdb7-4e84-84ad-52dc8664b19e', false, 'system', '2020-03-12 14:01:58.743', 'system', '2020-03-12 14:01:58.743', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('d6edb0a9-9b89-48ad-85ef-5618d2c53619', '003', 'DUE FROM PHILHEALTH', false, '', '2db90cd8-bdb7-4e84-84ad-52dc8664b19e', false, 'system', '2020-03-12 14:01:58.753', 'system', '2020-03-12 14:01:58.753', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('3af3a895-0aff-4102-88e8-38c7b753c5df', '001', 'PHIC-HCI', false, 'PHIC_HCI', 'd6edb0a9-9b89-48ad-85ef-5618d2c53619', false, 'system', '2020-03-12 14:01:58.758', 'system', '2020-03-12 14:01:58.758', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('80646b55-b45b-4764-98b1-30e614aa5784', '002', 'PHIC-PF', false, 'PHIC_PF', 'd6edb0a9-9b89-48ad-85ef-5618d2c53619', false, 'system', '2020-03-12 14:01:58.763', 'system', '2020-03-12 14:01:58.763', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('15c4025a-1937-4583-89c6-9228eb4978d1', '004', 'DUE FROM HMO/GOV''''T/CORP/INST RECEIVABLES', false, 'COMPANY_ACCOUNT', '2db90cd8-bdb7-4e84-84ad-52dc8664b19e', false, 'system', '2020-03-12 14:01:58.769', 'system', '2020-03-12 14:01:58.769', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('a0427552-ee39-4b48-906a-eb111ca4a7e8', '005', 'OTHER ACCOUNT RECEIVABLES', false, '', '2db90cd8-bdb7-4e84-84ad-52dc8664b19e', false, 'system', '2020-03-12 14:01:58.773', 'system', '2020-03-12 14:01:58.773', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('d75550c1-d08e-4da6-bcbd-f8ce099a157f', '04', 'ALLOWANCE FOR BAD DEBTS', true, '', '9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', false, 'system', '2020-03-12 14:01:58.778', 'system', '2020-03-12 14:01:58.778', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('ac0af54d-c1ca-4b25-832c-5dcf9e5c3352', '001', 'INPATIENT INDIVIDUAL ACCOUNT RECEIVABLES', false, 'AR_DEBIT_INPATIENT', 'd75550c1-d08e-4da6-bcbd-f8ce099a157f', false, 'system', '2020-03-12 14:01:58.783', 'system', '2020-03-12 14:01:58.783', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('3b8f05f2-1751-4612-bda0-7d941daebe11', '002', 'OUTPATIENT INDIVIDUAL ACCOUNT RECEIVABLES', false, 'AR_DEBIT_OUTPATIENT', 'd75550c1-d08e-4da6-bcbd-f8ce099a157f', false, 'system', '2020-03-12 14:01:58.787', 'system', '2020-03-12 14:01:58.787', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('aa8b9c1d-1d42-4bc7-a6b3-01578ec01e65', '003', 'DUE FROM PHILHEALTH', false, '', 'd75550c1-d08e-4da6-bcbd-f8ce099a157f', false, 'system', '2020-03-12 14:01:58.793', 'system', '2020-03-12 14:01:58.793', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('7056107a-b510-4026-b2c5-3fa1c3235284', '001', 'PHIC-HCI', false, 'PHIC_HCI', 'aa8b9c1d-1d42-4bc7-a6b3-01578ec01e65', false, 'system', '2020-03-12 14:01:58.799', 'system', '2020-03-12 14:01:58.799', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('a10b4446-a337-4a62-8541-8dd9fc7b92b6', '002', 'PHIC-PF', false, 'PHIC_PF', 'aa8b9c1d-1d42-4bc7-a6b3-01578ec01e65', false, 'system', '2020-03-12 14:01:58.806', 'system', '2020-03-12 14:01:58.806', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('03a1e90a-4b74-4d0a-810f-56b4b7a827d5', '004', 'DUE FROM HMO/GOV''''T/CORP/INST RECEIVABLES', false, 'COMPANY_ACCOUNT', 'd75550c1-d08e-4da6-bcbd-f8ce099a157f', false, 'system', '2020-03-12 14:01:58.812', 'system', '2020-03-12 14:01:58.812', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('27cd2371-8738-4fc4-9cbd-bdcecadc7e18', '005', 'OTHER ACCOUNT RECEIVABLES', false, '', 'd75550c1-d08e-4da6-bcbd-f8ce099a157f', false, 'system', '2020-03-12 14:01:58.817', 'system', '2020-03-12 14:01:58.817', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('23d7f07a-2f8b-47b1-8d67-5e6017d07953', '05', 'OTHER RECEIVABLES', true, '', '9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', false, 'system', '2020-03-12 14:01:58.822', 'system', '2020-03-12 14:01:58.822', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('b64f362e-63bb-42ef-a284-c7127c3d178b', '001', 'ADVANCES TO EMPLOYEES (LIQUIDATION)', false, '', '23d7f07a-2f8b-47b1-8d67-5e6017d07953', false, 'system', '2020-03-12 14:01:58.827', 'system', '2020-03-12 14:01:58.827', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('65d1ab40-d5b3-4bb8-8209-3094b8fdb80d', '002', 'ADVANCES TO ADMIN & MANAGEMENT (LIQUIDATION)', false, '', '23d7f07a-2f8b-47b1-8d67-5e6017d07953', false, 'system', '2020-03-12 14:01:58.832', 'system', '2020-03-12 14:01:58.832', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('1e4cd493-1852-43f6-9d4a-60e849371c3f', '003', 'ADVANCES TO SUPPLIERS', false, '', '23d7f07a-2f8b-47b1-8d67-5e6017d07953', false, 'system', '2020-03-12 14:01:58.839', 'system', '2020-03-12 14:01:58.839', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('0de6b51c-1672-46c1-926c-416136c7793c', '004', 'ADVANCES TO CONTRACTORS', false, '', '23d7f07a-2f8b-47b1-8d67-5e6017d07953', false, 'system', '2020-03-12 14:01:58.844', 'system', '2020-03-12 14:01:58.844', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('b361a239-d1c2-45d3-b568-6bb609a7e09b', '005', 'OTHER RECEIVABLES', false, '', '23d7f07a-2f8b-47b1-8d67-5e6017d07953', false, 'system', '2020-03-12 14:01:58.850', 'system', '2020-03-12 14:01:58.850', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('cccff3c0-7ce9-4acc-a8ef-5614831f2553', '06', 'NOTES RECEIVABLES', true, '', '9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', false, 'system', '2020-03-12 14:01:58.855', 'system', '2020-03-12 14:01:58.855', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('b97d4183-cf7b-4a47-a438-1a92d609f67f', '001', 'PROMISSORY NOTE FROM PATIENTS', false, 'PROMISSORY_NOTES', 'cccff3c0-7ce9-4acc-a8ef-5614831f2553', false, 'system', '2020-03-12 14:01:58.860', 'system', '2020-03-12 14:01:58.860', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('e5d1338c-1674-4531-a4c1-e355e6a52908', '07', 'SUBSCRIPTION RECEIVABLES', true, '', '9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', false, 'system', '2020-03-12 14:01:58.865', 'system', '2020-03-12 14:01:58.865', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('f1edbad0-e609-4e6b-b9ba-fa29c2c02710', '08', 'INVENTORY', true, 'COST_CENTER,INVENTORY', '9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', false, 'system', '2020-03-12 14:01:58.871', 'system', '2020-03-12 14:01:58.871', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('d3bdea97-e869-491a-ae45-f36a4e962933', '09', 'PREPAID EXPENSES AND OTHER CURRENT ASSETS', true, '', '9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', false, 'system', '2020-03-12 14:01:58.875', 'system', '2020-03-12 14:01:58.875', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('02f50f0e-a72b-4f6f-a02c-702ceb29f43e', '001', 'PREPAID INSURANCE', false, '', 'd3bdea97-e869-491a-ae45-f36a4e962933', false, 'system', '2020-03-12 14:01:58.880', 'system', '2020-03-12 14:01:58.880', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('8dfcc77b-c783-4050-af7e-f5661b302e9e', '002', 'PREPAID INTEREST', false, '', 'd3bdea97-e869-491a-ae45-f36a4e962933', false, 'system', '2020-03-12 14:01:58.885', 'system', '2020-03-12 14:01:58.885', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('5147d0f8-ac99-4c74-8632-c2659cbfc01b', '003', 'PREPAID RENT', false, '', 'd3bdea97-e869-491a-ae45-f36a4e962933', false, 'system', '2020-03-12 14:01:58.891', 'system', '2020-03-12 14:01:58.891', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('e531d53b-b3b9-4667-8e28-dd4edb057222', '004', 'PREPAID TAXES', false, '', 'd3bdea97-e869-491a-ae45-f36a4e962933', false, 'system', '2020-03-12 14:01:58.896', 'system', '2020-03-12 14:01:58.896', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('705d9609-cc04-42c2-a6e3-98701b89438f', '005', 'PREPAID SERVICE CONTRACTS', false, '', 'd3bdea97-e869-491a-ae45-f36a4e962933', false, 'system', '2020-03-12 14:01:58.901', 'system', '2020-03-12 14:01:58.901', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('873ae5b9-b687-48fa-aef3-dade01ac06f8', '006', 'PREPAID PENSION PLAN EXPENSE', false, '', 'd3bdea97-e869-491a-ae45-f36a4e962933', false, 'system', '2020-03-12 14:01:58.906', 'system', '2020-03-12 14:01:58.906', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('8ed1b84f-f9c8-4fdf-b43c-4c01ca2d9081', '007', 'OTHER PREPAID EXPENSES', false, '', 'd3bdea97-e869-491a-ae45-f36a4e962933', false, 'system', '2020-03-12 14:01:58.910', 'system', '2020-03-12 14:01:58.910', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('8465752e-0c78-4abb-add9-fe26ae97a9fb', '008', 'INPUT TAX', false, 'INPUT_TAX', 'd3bdea97-e869-491a-ae45-f36a4e962933', false, 'system', '2020-03-12 14:01:58.915', 'system', '2020-03-12 14:01:58.915', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('6bfdb143-87c2-45d7-85e6-efd773998387', '009', 'OTHER CURRENT ASSETS', false, '', 'd3bdea97-e869-491a-ae45-f36a4e962933', false, 'system', '2020-03-12 14:01:58.920', 'system', '2020-03-12 14:01:58.920', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('dba9bb6a-859c-4eee-b21e-2910b5680558', '009', 'PREPAID SUPPLIES', false, 'COST_CENTER', 'd3bdea97-e869-491a-ae45-f36a4e962933', false, 'system', '2020-03-12 14:01:58.926', 'system', '2020-03-12 14:01:58.926', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c251c93a-6809-4706-9136-4753411b568d', '10', 'ACCRUED INTEREST', true, '', '9476a6e1-7fae-4e28-92e0-9a0c936ebe0a', false, 'system', '2020-03-12 14:01:58.930', 'system', '2020-03-12 14:01:58.930', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('002eddc0-9514-4e0f-af36-83cf9b1d80ab', '001', 'ACCRUED INTEREST', false, 'ACCRUED_INTEREST', 'c251c93a-6809-4706-9136-4753411b568d', false, 'system', '2020-03-12 14:01:58.936', 'system', '2020-03-12 14:01:58.936', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('48c2c4b2-671c-4d83-91cc-c98cebb35809', '02', 'NON-CURRENT ASSETS', true, 'FS_FINPOS_NONCURRENT', '2a0d7e40-c25a-4e1c-833e-9535e3a6da0b', false, 'system', '2020-03-12 14:01:58.940', 'system', '2020-03-12 14:01:58.940', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c6883839-aa32-468e-8423-16fb8cde3040', '01', 'LAND', true, '', '48c2c4b2-671c-4d83-91cc-c98cebb35809', false, 'system', '2020-03-12 14:01:58.946', 'system', '2020-03-12 14:01:58.946', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('4617456b-8059-425e-91ad-f9c57d497a38', '02', 'LAND IMPROVEMENTS', true, '', '48c2c4b2-671c-4d83-91cc-c98cebb35809', false, 'system', '2020-03-12 14:01:58.951', 'system', '2020-03-12 14:01:58.951', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('fba67b26-9633-4533-aac9-694e9cd72c8d', '001', 'PARKING LOTS', false, '', '4617456b-8059-425e-91ad-f9c57d497a38', false, 'system', '2020-03-12 14:01:58.956', 'system', '2020-03-12 14:01:58.956', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('e2a73a7f-2b4b-4f02-91e0-4f68525a30e6', '002', 'ACCUMULATED DEPRECIATION-PARKING LOT IMPROVEMENTS', false, '', '4617456b-8059-425e-91ad-f9c57d497a38', false, 'system', '2020-03-12 14:01:58.960', 'system', '2020-03-12 14:01:58.960', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('5c3241fb-ba59-4c05-b8da-2e006b7a4ee3', '003', 'OTHER LAND IMPROVEMENTS', false, '', '4617456b-8059-425e-91ad-f9c57d497a38', false, 'system', '2020-03-12 14:01:58.965', 'system', '2020-03-12 14:01:58.965', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('147d8107-eaca-4c55-b37f-670414f25248', '004', 'ACCUMULATED DEPRECIATION-OTHER LAND IMPROVEMENTS', false, '', '4617456b-8059-425e-91ad-f9c57d497a38', false, 'system', '2020-03-12 14:01:58.970', 'system', '2020-03-12 14:01:58.970', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c49a4fa7-abc1-4796-89b0-fc103c2de98c', '03', 'BUILDINGS', true, '', '48c2c4b2-671c-4d83-91cc-c98cebb35809', false, 'system', '2020-03-12 14:01:58.974', 'system', '2020-03-12 14:01:58.974', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('9311519e-28b7-4c11-a3cb-53959cddd0f1', '001', 'HOSPITAL', false, '', 'c49a4fa7-abc1-4796-89b0-fc103c2de98c', false, 'system', '2020-03-12 14:01:58.979', 'system', '2020-03-12 14:01:58.979', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('e85fdf4c-564e-4408-99ca-b55de5d4faae', '002', 'ACCUMULATED DEPRECIATION-HOSPITAL', false, '', 'c49a4fa7-abc1-4796-89b0-fc103c2de98c', false, 'system', '2020-03-12 14:01:58.985', 'system', '2020-03-12 14:01:58.985', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('aebfc0a2-fb9e-49fa-845d-74267473af3f', '003', 'ELEVATORS', false, '', 'c49a4fa7-abc1-4796-89b0-fc103c2de98c', false, 'system', '2020-03-12 14:01:58.990', 'system', '2020-03-12 14:01:58.990', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('08ce0ba5-f2dc-4a29-857b-33dbe96ffa25', '004', 'ACCUMULATED DEPRECIATION-ELEVATORS', false, '', 'c49a4fa7-abc1-4796-89b0-fc103c2de98c', false, 'system', '2020-03-12 14:01:58.995', 'system', '2020-03-12 14:01:58.995', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('66be0335-d4b0-4e36-8de7-64ac145a45d2', '005', 'POWERHOUSE', false, '', 'c49a4fa7-abc1-4796-89b0-fc103c2de98c', false, 'system', '2020-03-12 14:01:59.000', 'system', '2020-03-12 14:01:59.000', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('48e959ce-a7e6-4025-b513-36c4bcf06987', '006', 'ACCUMULATED DEPRECIATION-POWERHOUSE', false, '', 'c49a4fa7-abc1-4796-89b0-fc103c2de98c', false, 'system', '2020-03-12 14:01:59.005', 'system', '2020-03-12 14:01:59.005', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('2df279b9-e54d-4b04-beda-6fc4f05ca075', '04', 'LEASEHOLD IMPROVEMENTS', true, '', '48c2c4b2-671c-4d83-91cc-c98cebb35809', false, 'system', '2020-03-12 14:01:59.010', 'system', '2020-03-12 14:01:59.010', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('2b50d179-c0b8-48b1-bd19-8628cfb311d4', '05', 'MAJOR MOVABLE EQUIPMENTS', true, 'FIXED_ASSETS', '48c2c4b2-671c-4d83-91cc-c98cebb35809', false, 'system', '2020-03-12 14:01:59.014', 'system', '2020-03-12 14:01:59.014', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('50a77e14-1bdb-45ea-88a9-428a15cf4fb1', '001', 'TRANSPORTATION/ MOBILE EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.019', 'system', '2020-03-12 14:01:59.019', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('66223a19-27d9-40b9-a2b2-8a5b89cd5a77', '002', 'ACCUMULATED DEPRECIATION -TRANSPORTATION/ MOBILE EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.024', 'system', '2020-03-12 14:01:59.024', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('49bde6b2-5d52-47e5-92e6-abf211bfbdcd', '003', 'MEDICAL EQUIPMENTS', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.028', 'system', '2020-03-12 14:01:59.028', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('2b487168-549e-4bab-bec2-6efc11ea3547', '004', 'ACCUMULATED DEPRECIATION -MEDICAL EQUIPMENTS', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.033', 'system', '2020-03-12 14:01:59.033', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('15630364-3809-4abd-8a3b-fd9e03c0d852', '005', 'HOSPITAL FURNITURE AND FIXTURES', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.036', 'system', '2020-03-12 14:01:59.036', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('53bd3a0c-c224-4fb5-8ffb-54a5fb2e7ce6', '006', 'ACCUMULATED DEPRECIATION -HOSPITAL FURNITURE AND FIXTURES', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.042', 'system', '2020-03-12 14:01:59.042', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('dc526664-1081-490c-8a05-65da5e4b8424', '007', 'HOSPITAL EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.047', 'system', '2020-03-12 14:01:59.047', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('d42f8e29-32ab-411a-b2e1-7575f2283ed6', '008', 'ACCUMULATED DEPRECIATION -HOSPITAL EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.052', 'system', '2020-03-12 14:01:59.052', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('e4c95988-48e9-4d34-9fe5-c6e474b43f17', '009', 'OFFICE FURNITURES & FIXTURES', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.058', 'system', '2020-03-12 14:01:59.058', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('0a3b0642-6d3a-45e9-a2e7-bf7bd9940dd4', '010', 'ACCUMULATED DEPRECIATION -OFFICE FURNITURES & FIXTURES', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.063', 'system', '2020-03-12 14:01:59.063', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('ac9a5064-1a36-4811-8b87-4f0f79b46760', '011', 'OFFICE EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.066', 'system', '2020-03-12 14:01:59.066', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('1ea7d7d5-7d1b-4bad-bafc-2224e724e536', '012', 'ACCUMULATED DEPRECIATION -OFFICE EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.072', 'system', '2020-03-12 14:01:59.072', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('53d82975-7200-42ef-9099-4845a77beebd', '013', 'ENGINEERING TOOLS AND EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.077', 'system', '2020-03-12 14:01:59.077', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('4f8c212d-8355-4091-9058-b827261858d4', '014', 'ACCUMULATED DEPRECIATION -ENGINEERING TOOLS AND EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.082', 'system', '2020-03-12 14:01:59.082', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('51ab5d21-ffd7-46b9-8319-3217d60b38d4', '015', 'GENERAL SERVICE TOOLS AND EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.086', 'system', '2020-03-12 14:01:59.086', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('0beb1773-560a-4929-a8e6-b0480ac26c20', '016', 'ACCUMULATED DEPRECIATION -GENERAL SERVICE TOOLS AND EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.091', 'system', '2020-03-12 14:01:59.091', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('a7ff507d-6585-404a-b48f-be479a052025', '017', 'DIETARY AND KITCHEN EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.095', 'system', '2020-03-12 14:01:59.095', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('5bb3938a-cb92-4142-a40d-b2ed4199e373', '018', 'ACCUMULATED DEPRECIATION -DIETARY AND KITCHEN EQUIPMENT', false, 'COST_CENTER', '2b50d179-c0b8-48b1-bd19-8628cfb311d4', false, 'system', '2020-03-12 14:01:59.100', 'system', '2020-03-12 14:01:59.100', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('314adfb1-efea-4e3d-b042-0430b9c83a41', '06', 'MINOR MOVABLE EQUIPMENTS', true, 'FIXED_ASSETS', '48c2c4b2-671c-4d83-91cc-c98cebb35809', false, 'system', '2020-03-12 14:01:59.106', 'system', '2020-03-12 14:01:59.106', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c8e35fed-bf40-46bf-8345-d2bdcf6e970d', '001', 'HOSPITAL MINOR MOVABLE EQUIPMENT', false, 'COST_CENTER', '314adfb1-efea-4e3d-b042-0430b9c83a41', false, 'system', '2020-03-12 14:01:59.111', 'system', '2020-03-12 14:01:59.111', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('e7563b50-19db-42ed-80b6-60fcc7927a04', '002', 'ACCUMULATED DEPRECIATION -HOSPITAL MINOR MOVABLE EQUIPMENT', false, 'COST_CENTER', '314adfb1-efea-4e3d-b042-0430b9c83a41', false, 'system', '2020-03-12 14:01:59.115', 'system', '2020-03-12 14:01:59.115', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('a124adb7-95b2-433b-a319-0bf9f5a67943', '003', 'DIETARY MINOR MOVABLE EQUIPMENT', false, 'COST_CENTER', '314adfb1-efea-4e3d-b042-0430b9c83a41', false, 'system', '2020-03-12 14:01:59.119', 'system', '2020-03-12 14:01:59.119', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('dbddcaee-ec46-47e2-afa7-0ca846b8c0b5', '004', 'ACCUMULATED DEPRECIATION -DIETARY MINOR MOVABLE EQUIPMENT', false, 'COST_CENTER', '314adfb1-efea-4e3d-b042-0430b9c83a41', false, 'system', '2020-03-12 14:01:59.124', 'system', '2020-03-12 14:01:59.124', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('64b671b6-8217-4d86-a96e-300d831675f1', '005', 'ADMIN & OFFICE MINOR MOVABLE EQUIPMENT', false, 'COST_CENTER', '314adfb1-efea-4e3d-b042-0430b9c83a41', false, 'system', '2020-03-12 14:01:59.129', 'system', '2020-03-12 14:01:59.129', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('3348bc92-6b23-4217-b537-92e683f319fb', '006', 'ACCUMULATED DEPRECIATION - ADMIN & OFFICE MINOR MOVABLE EQUIPMENT', false, 'COST_CENTER', '314adfb1-efea-4e3d-b042-0430b9c83a41', false, 'system', '2020-03-12 14:01:59.134', 'system', '2020-03-12 14:01:59.134', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('8aa9c695-cefc-48d0-bb94-4a1bcc1ffe71', '07', 'SOFTWARE', true, '', '48c2c4b2-671c-4d83-91cc-c98cebb35809', false, 'system', '2020-03-12 14:01:59.138', 'system', '2020-03-12 14:01:59.138', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('4c0fb363-d418-41e0-a537-c05efaed962b', '001', 'Accumulated Amortization', false, '', '8aa9c695-cefc-48d0-bb94-4a1bcc1ffe71', false, 'system', '2020-03-12 14:01:59.142', 'system', '2020-03-12 14:01:59.142', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('930d5226-425f-4095-82cc-3fb06c9dc275', '08', 'CONSTRUCTION-IN-PROGRESS', true, '', '48c2c4b2-671c-4d83-91cc-c98cebb35809', false, 'system', '2020-03-12 14:01:59.147', 'system', '2020-03-12 14:01:59.147', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('7b972681-4a9a-4cbb-9e15-77402e108c90', '001', 'CIP - PARKING LOT IMPROVEMENTS', false, '', '930d5226-425f-4095-82cc-3fb06c9dc275', false, 'system', '2020-03-12 14:01:59.152', 'system', '2020-03-12 14:01:59.152', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('87ea2624-3fe8-40aa-8819-6a25cc7d2264', '002', 'CIP - OTHER LAND IMPROVEMENTS', false, '', '930d5226-425f-4095-82cc-3fb06c9dc275', false, 'system', '2020-03-12 14:01:59.156', 'system', '2020-03-12 14:01:59.156', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('a731f34f-e62e-4693-a059-1b0c71d3e642', '003', 'CIP -  HOSPITAL BUILDING', false, '', '930d5226-425f-4095-82cc-3fb06c9dc275', false, 'system', '2020-03-12 14:01:59.161', 'system', '2020-03-12 14:01:59.161', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('1c6b94be-d26a-4d6a-bcee-0aa390d96321', '004', 'CIP -  POWERHOUSE', false, '', '930d5226-425f-4095-82cc-3fb06c9dc275', false, 'system', '2020-03-12 14:01:59.167', 'system', '2020-03-12 14:01:59.167', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('7229bf06-e55a-4a18-85c4-d6b18b546d81', '005', 'CIP -  ELEVATORS', false, '', '930d5226-425f-4095-82cc-3fb06c9dc275', false, 'system', '2020-03-12 14:01:59.172', 'system', '2020-03-12 14:01:59.172', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('87a04952-e56e-49f2-98d4-3382281d3061', '02', 'LIABILITIES', true, 'LIABILITIES', NULL, false, 'system', '2020-03-12 14:01:59.177', 'system', '2020-03-12 14:01:59.177', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('10578aca-4006-4d8d-a223-e53f2db0c1ac', '01', 'CURRENT LIABILITIES', true, 'FS_FINPOS_CURRENT_LIABILITIES', '87a04952-e56e-49f2-98d4-3382281d3061', false, 'system', '2020-03-12 14:01:59.183', 'system', '2020-03-12 14:01:59.183', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('14429f37-6be5-4f71-80d1-ca93643db1a4', '01', 'NOTES PAYABLE', true, '', '10578aca-4006-4d8d-a223-e53f2db0c1ac', false, 'system', '2020-03-12 14:01:59.187', 'system', '2020-03-12 14:01:59.187', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('f743d541-c1c9-4c09-87b6-cebd979af12a', '001', 'NOTES PAYABLE TO VENDOR/ SUPPLIERS', false, 'NP_VENDOR', '14429f37-6be5-4f71-80d1-ca93643db1a4', false, 'system', '2020-03-12 14:01:59.191', 'system', '2020-03-12 14:01:59.191', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('216319a9-aadd-4a02-a17e-dbc8ad636bd8', '002', 'NOTES PAYABLE TO CONTRACTORS', false, 'NP_CONTRACTORS', '14429f37-6be5-4f71-80d1-ca93643db1a4', false, 'system', '2020-03-12 14:01:59.196', 'system', '2020-03-12 14:01:59.196', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('76abec63-a736-45b2-bb48-eea418a94ed2', '003', 'NOTES PAYABLE TO BANKS', false, 'NP_BANKS,BANK_ACCOUNTS', '14429f37-6be5-4f71-80d1-ca93643db1a4', false, 'system', '2020-03-12 14:01:59.198', 'system', '2020-03-12 14:01:59.198', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c3e448eb-63fb-40f6-adc7-ce0d2ad94d59', '004', 'OTHER NOTES PAYABLE', false, '', '14429f37-6be5-4f71-80d1-ca93643db1a4', false, 'system', '2020-03-12 14:01:59.205', 'system', '2020-03-12 14:01:59.205', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('f98e27b7-34fa-4b78-b28b-306de02d4813', '02', 'LOANS PAYABLE', true, '', '10578aca-4006-4d8d-a223-e53f2db0c1ac', false, 'system', '2020-03-12 14:01:59.210', 'system', '2020-03-12 14:01:59.210', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('054cb6d8-bf7a-43de-a6eb-b616690d70e4', '001', 'LOANS PAYABLE TO BANKS', false, 'LP_BANKS,BANK_ACCOUNTS', 'f98e27b7-34fa-4b78-b28b-306de02d4813', false, 'system', '2020-03-12 14:01:59.214', 'system', '2020-03-12 14:01:59.214', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('7d82dfae-037a-4d26-abe0-00223e685b33', '002', 'OTHER LOANS PAYABLE', false, '', 'f98e27b7-34fa-4b78-b28b-306de02d4813', false, 'system', '2020-03-12 14:01:59.218', 'system', '2020-03-12 14:01:59.218', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('141c08c2-7244-4d7b-9326-2b22ecafbe1f', '003', 'LOANS INTEREST  PAYABLE', false, '', 'f98e27b7-34fa-4b78-b28b-306de02d4813', false, 'system', '2020-03-12 14:01:59.222', 'system', '2020-03-12 14:01:59.222', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('e0529950-c57d-422e-8499-36aae15e1cad', '03', 'CURRENT PORTION OF LONG TERM DEBT', true, '', '10578aca-4006-4d8d-a223-e53f2db0c1ac', false, 'system', '2020-03-12 14:01:59.226', 'system', '2020-03-12 14:01:59.226', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('710e6420-157f-41b2-b8ca-086cf96e679a', '001', 'CURRENT PORTION OF LONG TERM DEBT TO VENDOR/ SUPPLIERS', false, 'CP_VENDOR', 'e0529950-c57d-422e-8499-36aae15e1cad', false, 'system', '2020-03-12 14:01:59.231', 'system', '2020-03-12 14:01:59.231', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('626e8d28-f933-4bca-a5f0-4980ad9117db', '02', 'SALES', true, '', '1aa12f41-91e9-4574-867f-e102ad2b13e5', false, 'system', '2020-03-12 14:01:59.490', 'system', '2020-03-12 14:01:59.490', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('5f383c2a-92c1-4bdd-820a-8df57ff58ec8', '002', 'CURRENT PORTION OF LONG TERM DEBT TO CONTRACTORS', false, 'CP_CONTRACTORS', 'e0529950-c57d-422e-8499-36aae15e1cad', false, 'system', '2020-03-12 14:01:59.235', 'system', '2020-03-12 14:01:59.235', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('3f8f9c17-c51a-4e71-9763-337a8e93d715', '003', 'CURRENT PORTION OF LONG TERM DEBT TO BANKS', false, 'CP_BANKS,BANK_ACCOUNTS', 'e0529950-c57d-422e-8499-36aae15e1cad', false, 'system', '2020-03-12 14:01:59.240', 'system', '2020-03-12 14:01:59.240', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('76a652cc-bc14-47a3-97fe-3f9b14463f7c', '04', 'ACCOUNTS PAYABLE', true, '', '10578aca-4006-4d8d-a223-e53f2db0c1ac', false, 'system', '2020-03-12 14:01:59.243', 'system', '2020-03-12 14:01:59.243', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('0303a393-a60c-478e-9ebf-4f488e7f3758', '001', 'ACCOUNTS PAYABLE TO VENDORS/ SUPPLIERS', false, 'AP_VENDOR', '76a652cc-bc14-47a3-97fe-3f9b14463f7c', false, 'system', '2020-03-12 14:01:59.247', 'system', '2020-03-12 14:01:59.247', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('a7a0d6ad-bb07-4e6b-b4ec-5cdf967eb032', '002', 'ACCOUNTS PAYABLE TO CONTRACTORS', false, 'AP_CONTRACTOR', '76a652cc-bc14-47a3-97fe-3f9b14463f7c', false, 'system', '2020-03-12 14:01:59.252', 'system', '2020-03-12 14:01:59.252', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('d8de1757-a1e7-475f-8586-ae3d993d63f0', '003', 'PROVISION FOR PAYABLE TO SUPPLIER', false, 'AP_PROVISION_VENDOR', '76a652cc-bc14-47a3-97fe-3f9b14463f7c', false, 'system', '2020-03-12 14:01:59.256', 'system', '2020-03-12 14:01:59.256', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('93e56eb1-1ccc-4ee9-bea5-40a48b40bcea', '004', 'PROVISION FOR OTHER PAYABLES', false, 'OTHER_PAYABLE_TRACKING', '76a652cc-bc14-47a3-97fe-3f9b14463f7c', false, 'system', '2020-03-12 14:01:59.261', 'system', '2020-03-12 14:01:59.261', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('dd33856f-aa14-4bb6-ba50-dcea0ea3e6df', '005', 'OTHER ACCOUNTS PAYABLE', false, '', '76a652cc-bc14-47a3-97fe-3f9b14463f7c', false, 'system', '2020-03-12 14:01:59.264', 'system', '2020-03-12 14:01:59.264', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('acf36aaf-d074-40e0-837b-b8fb923389cc', '006', 'PROVISION FOR PAYABLE TO CONTRACTORS', false, 'AP_PROVISION_CONTRACTOR', '76a652cc-bc14-47a3-97fe-3f9b14463f7c', false, 'system', '2020-03-12 14:01:59.270', 'system', '2020-03-12 14:01:59.270', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('5a983e50-8bc1-4418-9a2d-e06755841f84', '007', 'SUSPEND ACCOUNT- PHILHEALTH', false, 'SUSPEND_ACCOUNT_PHIC', '76a652cc-bc14-47a3-97fe-3f9b14463f7c', false, 'system', '2020-03-12 14:01:59.274', 'system', '2020-03-12 14:01:59.274', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('6c67f3ec-a85c-4ecb-a0f4-0bb81ef5889b', '008', 'SUSPEND ACCOUNT- OTHERS', false, 'DEFERRED_INCOME', '76a652cc-bc14-47a3-97fe-3f9b14463f7c', false, 'system', '2020-03-12 14:01:59.279', 'system', '2020-03-12 14:01:59.279', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('1cd9ae48-3683-4955-952d-ccdfc6287dfe', '05', 'COMPENSATION AND RELATED LIABILITY', true, '', '10578aca-4006-4d8d-a223-e53f2db0c1ac', false, 'system', '2020-03-12 14:01:59.284', 'system', '2020-03-12 14:01:59.284', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('dac8edcd-515a-42f3-9be6-24c51427abf1', '001', 'ACCRUED SALARIES AND WAGES', false, 'BASIC_SALARY', '1cd9ae48-3683-4955-952d-ccdfc6287dfe', false, 'system', '2020-03-12 14:01:59.289', 'system', '2020-03-12 14:01:59.289', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('18ab7d08-f4bf-4fb4-9347-9f1996b64318', '002', 'ACCRUED VACATION,HOLIDAY AND SICK PAY', false, 'BASIC_VL', '1cd9ae48-3683-4955-952d-ccdfc6287dfe', false, 'system', '2020-03-12 14:01:59.294', 'system', '2020-03-12 14:01:59.294', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('da8bb79a-7634-4f99-88bf-0f284a5cd0e5', '003', 'WITHHOLDING TAX PAYABLE- COMPENSATION', false, 'WTX_COMPENSATION', '1cd9ae48-3683-4955-952d-ccdfc6287dfe', false, 'system', '2020-03-12 14:01:59.302', 'system', '2020-03-12 14:01:59.302', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('f597d9db-70ec-4a3d-99bd-cf463eef28b2', '004', 'PHILHEALTH PREMIUM PAYABLE', false, 'PHIC_PAYABLE', '1cd9ae48-3683-4955-952d-ccdfc6287dfe', false, 'system', '2020-03-12 14:01:59.308', 'system', '2020-03-12 14:01:59.308', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('03926b44-b128-4082-9628-47aa9d6e6a4b', '005', 'PAG-IBIG/HDMF PREMIUM PAYABLE', false, 'HDMF_PREMIUM_PAYABLE', '1cd9ae48-3683-4955-952d-ccdfc6287dfe', false, 'system', '2020-03-12 14:01:59.313', 'system', '2020-03-12 14:01:59.313', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('96b91fe5-5c44-4011-b452-47b905ac5dc9', '006', 'SSS LOANS PAYABLE', false, 'SSS_PAYABLE', '1cd9ae48-3683-4955-952d-ccdfc6287dfe', false, 'system', '2020-03-12 14:01:59.318', 'system', '2020-03-12 14:01:59.318', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('dd05ff46-2df2-4d7b-a8b7-b51520379e10', '007', 'PAG-IBIG/HDMF LOANS PAYABLE', false, 'HDMF_LOANS_PAYABLE', '1cd9ae48-3683-4955-952d-ccdfc6287dfe', false, 'system', '2020-03-12 14:01:59.322', 'system', '2020-03-12 14:01:59.322', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('94b77eb8-00cc-42f1-bee9-b8b467bafec7', '008', 'OTHER PAYROLL DEDUCTIONS', false, '', '1cd9ae48-3683-4955-952d-ccdfc6287dfe', false, 'system', '2020-03-12 14:01:59.327', 'system', '2020-03-12 14:01:59.327', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('6eef2527-03b6-4f91-be2b-8786213dcdf9', '009', 'SSS PREMIUM PAYABLE', false, '', '1cd9ae48-3683-4955-952d-ccdfc6287dfe', false, 'system', '2020-03-12 14:01:59.331', 'system', '2020-03-12 14:01:59.331', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c4f72335-69a6-4df0-b50d-ca15ae8a3fcd', '010', NULL, false, '', '1cd9ae48-3683-4955-952d-ccdfc6287dfe', false, 'system', '2020-03-12 14:01:59.336', 'system', '2020-03-12 14:01:59.336', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('5db306bf-2c3b-4c3d-8292-2e32c7df288c', '06', 'OTHER CURRENT PAYABLES', true, '', '10578aca-4006-4d8d-a223-e53f2db0c1ac', false, 'system', '2020-03-12 14:01:59.339', 'system', '2020-03-12 14:01:59.339', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('24975425-7a0d-42b4-b782-ab9dd69ce1ed', '001', 'OUTPUT TAXES', false, 'OUTPUT_TAX', '5db306bf-2c3b-4c3d-8292-2e32c7df288c', false, 'system', '2020-03-12 14:01:59.343', 'system', '2020-03-12 14:01:59.343', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('629d62ae-e002-42a1-8a0e-aac4ab63cc2b', '002', 'VAT PAYABLE', false, 'VAT_PAYABLE', '5db306bf-2c3b-4c3d-8292-2e32c7df288c', false, 'system', '2020-03-12 14:01:59.347', 'system', '2020-03-12 14:01:59.347', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('840ebdfc-aa1c-4acd-8e93-6ca662a29aae', '003', 'WITHHOLDING TAX PAYABLE  EXPANDED - DOCTORS FEE', false, 'DOCTORS_PF_WTX_EXPANDED', '5db306bf-2c3b-4c3d-8292-2e32c7df288c', false, 'system', '2020-03-12 14:01:59.352', 'system', '2020-03-12 14:01:59.352', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('b2a1f772-7cfb-4de1-acb4-84506c83d69d', '004', 'WITHHOLDING TAX PAYABLE  EXPANDED - OTHERS', false, 'OTHERS_WTX_EXPANDED', '5db306bf-2c3b-4c3d-8292-2e32c7df288c', false, 'system', '2020-03-12 14:01:59.356', 'system', '2020-03-12 14:01:59.356', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('be71c465-22c8-42d9-8739-656fe3ba122c', '005', 'DOCTOR''S PROFESSIONAL FEE LIABILITY', false, 'DOCTOR_ACCRUED_PF', '5db306bf-2c3b-4c3d-8292-2e32c7df288c', false, 'system', '2020-03-12 14:01:59.361', 'system', '2020-03-12 14:01:59.361', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('14ea675e-90ac-48ef-8a06-a05fc517a247', '006', 'READERS FEE LIABILITY', false, 'DOCTOR_ACCRUED_PF_READERS', '5db306bf-2c3b-4c3d-8292-2e32c7df288c', false, 'system', '2020-03-12 14:01:59.365', 'system', '2020-03-12 14:01:59.365', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('82363ed7-5a73-4e77-b96e-a617f613bc36', '007', 'PF TO DOCTORS', false, 'DOCTOR_PF', '5db306bf-2c3b-4c3d-8292-2e32c7df288c', false, 'system', '2020-03-12 14:01:59.369', 'system', '2020-03-12 14:01:59.369', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('abfe3bf6-5018-4604-ac64-b78f48c3451d', '07', 'TAX PAYABLE', true, '', '10578aca-4006-4d8d-a223-e53f2db0c1ac', false, 'system', '2020-03-12 14:01:59.375', 'system', '2020-03-12 14:01:59.375', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('987c2271-abc2-4f85-8caa-d6e5200653ab', '001', 'INCOME TAX PAYABLE', false, '', 'abfe3bf6-5018-4604-ac64-b78f48c3451d', false, 'system', '2020-03-12 14:01:59.378', 'system', '2020-03-12 14:01:59.378', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('f46ce2e8-4c05-441f-9cd7-881fb316e3b5', '002', 'PROPERTY TAX PAYABLE', false, '', 'abfe3bf6-5018-4604-ac64-b78f48c3451d', false, 'system', '2020-03-12 14:01:59.381', 'system', '2020-03-12 14:01:59.381', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('d500723e-b680-4290-8e11-0f7601e2ca5e', '02', 'NONCURRENT LIABILITIES', true, 'FS_FINPOS_NONCURRENT_LIABILITIES', '87a04952-e56e-49f2-98d4-3382281d3061', false, 'system', '2020-03-12 14:01:59.387', 'system', '2020-03-12 14:01:59.387', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('4617427d-9f19-4374-8175-8fbebe546e90', '01', 'LOANS PAYABLE', true, '', 'd500723e-b680-4290-8e11-0f7601e2ca5e', false, 'system', '2020-03-12 14:01:59.391', 'system', '2020-03-12 14:01:59.391', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('8fa5a8ef-987b-43f0-977e-7898d37ea058', '001', 'LOANS PAYABLE TO BANKS', false, 'LT_LP_BANKS,BANK_ACCOUNTS', '4617427d-9f19-4374-8175-8fbebe546e90', false, 'system', '2020-03-12 14:01:59.395', 'system', '2020-03-12 14:01:59.395', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('3baf781b-9442-4d1e-9ae0-32f66cfc1e2c', '002', 'OTHER LOANS PAYABLE', false, '', '4617427d-9f19-4374-8175-8fbebe546e90', false, 'system', '2020-03-12 14:01:59.399', 'system', '2020-03-12 14:01:59.399', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('1e8ff467-4b46-40dc-9eec-5a598b87ed49', '02', 'LOANS INTEREST PAYABLE', true, '', 'd500723e-b680-4290-8e11-0f7601e2ca5e', false, 'system', '2020-03-12 14:01:59.403', 'system', '2020-03-12 14:01:59.403', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('9cb94d7b-8de6-4854-a0d6-7066f6ed2b0f', '001', 'LOANS INTEREST PAYABLE TO BANKS', false, 'LI_LP_BANKS,BANK_ACCOUNTS', '1e8ff467-4b46-40dc-9eec-5a598b87ed49', false, 'system', '2020-03-12 14:01:59.407', 'system', '2020-03-12 14:01:59.407', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('e7da4c2d-a085-4419-b4d4-c063e45c2112', '03', 'CONSTRUCTION RETENTION PAYABLE', true, '', 'd500723e-b680-4290-8e11-0f7601e2ca5e', false, 'system', '2020-03-12 14:01:59.410', 'system', '2020-03-12 14:01:59.410', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('0020b903-220d-4918-b904-5255ca157ba1', '04', 'ADVANCES', true, '', 'd500723e-b680-4290-8e11-0f7601e2ca5e', false, 'system', '2020-03-12 14:01:59.415', 'system', '2020-03-12 14:01:59.415', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('49303d6d-0133-46a4-8936-e33f1e6c613a', '001', 'ADVANCES FROM PATIENTS', false, 'ADVANCES_PATIENTS', '0020b903-220d-4918-b904-5255ca157ba1', false, 'system', '2020-03-12 14:01:59.418', 'system', '2020-03-12 14:01:59.418', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('5ef74bcf-f99f-4a39-ad02-850957df8f3e', '002', 'ADVANCES FROM OTHERS', false, 'ADVANCES_OTHERS', '0020b903-220d-4918-b904-5255ca157ba1', false, 'system', '2020-03-12 14:01:59.423', 'system', '2020-03-12 14:01:59.423', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('0ef141d0-95a5-4a50-be74-612fe21718ab', '05', 'OTHER NONCURRENT LIABILITIES', true, '', 'd500723e-b680-4290-8e11-0f7601e2ca5e', false, 'system', '2020-03-12 14:01:59.428', 'system', '2020-03-12 14:01:59.428', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('13b671bd-33ff-4034-96e4-abfb0ba15a7d', '03', 'SHAREHOLDER''S EQUITY', true, 'SHAREHOLDERS,FS_FINPOS_SHAREHOLDERS', NULL, false, 'system', '2020-03-12 14:01:59.434', 'system', '2020-03-12 14:01:59.434', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('492ddd81-a271-4783-82c4-eb31753ea05e', '01', 'SUBSCRIBED CAPITAL STOCK', true, '', '13b671bd-33ff-4034-96e4-abfb0ba15a7d', false, 'system', '2020-03-12 14:01:59.438', 'system', '2020-03-12 14:01:59.438', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('14e88ada-d3f1-4cf5-80f3-2a681e410927', '02', 'ADDITIONAL PAID-IN CAPITAL', true, '', '13b671bd-33ff-4034-96e4-abfb0ba15a7d', false, 'system', '2020-03-12 14:01:59.441', 'system', '2020-03-12 14:01:59.441', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('1803fab0-cf70-44f4-b852-82aa77c69868', '03', 'RETAINED EARNINGS', true, '', '13b671bd-33ff-4034-96e4-abfb0ba15a7d', false, 'system', '2020-03-12 14:01:59.447', 'system', '2020-03-12 14:01:59.447', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('6fe7a200-6689-4a94-8673-fce70fe87977', '04', 'DEPOSIT FOR FUTURE SUBSCRIPTION', true, '', '13b671bd-33ff-4034-96e4-abfb0ba15a7d', false, 'system', '2020-03-12 14:01:59.451', 'system', '2020-03-12 14:01:59.451', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('a6dacd45-182f-498a-bb93-6e0308a15d86', '01', 'FUTURE SUBSCRIBERS', true, '', '6fe7a200-6689-4a94-8673-fce70fe87977', false, 'system', '2020-03-12 14:01:59.455', 'system', '2020-03-12 14:01:59.455', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c700ec35-27a3-4460-883c-1b4dfef6412a', '99', 'BEGINNING BALANCING', true, '', '13b671bd-33ff-4034-96e4-abfb0ba15a7d', false, 'system', '2020-03-12 14:01:59.459', 'system', '2020-03-12 14:01:59.459', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('189b9e39-78fa-41c7-b19c-ef4e00f88325', '01', 'HISTORICAL BALANCING', true, 'HISTORICAL_BALANCING', 'c700ec35-27a3-4460-883c-1b4dfef6412a', false, 'system', '2020-03-12 14:01:59.464', 'system', '2020-03-12 14:01:59.464', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('1aa12f41-91e9-4574-867f-e102ad2b13e5', '04', 'REVENUE', true, 'REVENUE', NULL, false, 'system', '2020-03-12 14:01:59.469', 'system', '2020-03-12 14:01:59.469', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('4bc8e3e0-8143-4522-87a8-ac857e48e9bd', '01', 'DAILY HOSPITAL AND AMBULATORY SERVICES', true, '', '1aa12f41-91e9-4574-867f-e102ad2b13e5', false, 'system', '2020-03-12 14:01:59.473', 'system', '2020-03-12 14:01:59.473', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('2d15d964-7f5d-4048-986d-16362bf40f17', '01', 'ROOMS', true, '', '4bc8e3e0-8143-4522-87a8-ac857e48e9bd', false, 'system', '2020-03-12 14:01:59.478', 'system', '2020-03-12 14:01:59.478', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('1c176494-2992-440a-b015-b91afce9d92e', '02', 'PHARMACY', true, '', '4bc8e3e0-8143-4522-87a8-ac857e48e9bd', false, 'system', '2020-03-12 14:01:59.482', 'system', '2020-03-12 14:01:59.482', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('94f83afe-8271-4348-8fbc-0a3ad77ced3d', '03', 'CSR', true, '', '4bc8e3e0-8143-4522-87a8-ac857e48e9bd', false, 'system', '2020-03-12 14:01:59.487', 'system', '2020-03-12 14:01:59.487', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('1979d1d0-cf5d-4ae4-a8ba-1ba95bbd81c8', '01', 'IN-PATIENT', true, 'REVENUESOURCE,REVENUE_INPATIENT,FS_PROFITLOSS_INPATIENT', '626e8d28-f933-4bca-a5f0-4980ad9117db', false, 'system', '2020-03-12 14:01:59.495', 'system', '2020-03-12 14:01:59.495', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('ece92bf6-f8da-4912-ae3c-b0b79d9561f2', '02', 'OUT-PATIENT', true, 'REVENUESOURCE,REVENUE_OUTPATIENT,FS_PROFITLOSS_OUTPATIENT', '626e8d28-f933-4bca-a5f0-4980ad9117db', false, 'system', '2020-03-12 14:01:59.500', 'system', '2020-03-12 14:01:59.500', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('176f3d85-946e-45c8-93ad-0fce9fe491ac', '05', 'COST OF SALES', true, 'COSTOFSALES', NULL, false, 'system', '2020-03-12 14:01:59.504', 'system', '2020-03-12 14:01:59.504', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('ec47501e-65db-462e-af45-1a8a8beeb7eb', '01', 'DAILY HOSPITAL AND AMBULATORY SERVICES', true, '', '176f3d85-946e-45c8-93ad-0fce9fe491ac', false, 'system', '2020-03-12 14:01:59.508', 'system', '2020-03-12 14:01:59.508', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c8a54c49-de52-4565-9c07-e7449448d23f', '01', 'ROOMS', true, '', 'ec47501e-65db-462e-af45-1a8a8beeb7eb', false, 'system', '2020-03-12 14:01:59.512', 'system', '2020-03-12 14:01:59.512', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c9fa0e24-3b33-4517-8766-4b09f6f142e2', '02', 'PHARMACY', true, '', 'ec47501e-65db-462e-af45-1a8a8beeb7eb', false, 'system', '2020-03-12 14:01:59.517', 'system', '2020-03-12 14:01:59.517', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('30cb8f6b-be31-4213-9862-d9a3a15fb0a8', '03', 'CSR', true, '', 'ec47501e-65db-462e-af45-1a8a8beeb7eb', false, 'system', '2020-03-12 14:01:59.522', 'system', '2020-03-12 14:01:59.522', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('6d887b1b-9a6b-45ed-8c4c-0a46bc2f1c54', '02', 'SALES', true, 'FS_PROFITLOSS_COSTOFSALES', '176f3d85-946e-45c8-93ad-0fce9fe491ac', false, 'system', '2020-03-12 14:01:59.526', 'system', '2020-03-12 14:01:59.526', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('411da2b8-e4c8-4e98-bbc6-544dbcba3a55', '01', 'IN-PATIENT', true, 'REVENUESOURCE,COST_INPATIENT', '6d887b1b-9a6b-45ed-8c4c-0a46bc2f1c54', false, 'system', '2020-03-12 14:01:59.530', 'system', '2020-03-12 14:01:59.530', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('78f378ab-0f35-40c8-9861-eda9d8e95dcd', '02', 'OUT-PATIENT', true, 'REVENUESOURCE,COST_OUTPATIENT', '6d887b1b-9a6b-45ed-8c4c-0a46bc2f1c54', false, 'system', '2020-03-12 14:01:59.536', 'system', '2020-03-12 14:01:59.536', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('25d3249f-9292-428f-b3a0-b0c78069a142', '03', 'INVENTORY COST', true, 'COST_CENTER,COST_INVENTORY', '6d887b1b-9a6b-45ed-8c4c-0a46bc2f1c54', false, 'system', '2020-03-12 14:01:59.539', 'system', '2020-03-12 14:01:59.539', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('4c36a29c-415a-4658-9797-4888ab35da0a', '04', 'TRANSCRIPTION/READER''S FEE', true, 'COST_CENTER,READERS_FEE', '6d887b1b-9a6b-45ed-8c4c-0a46bc2f1c54', false, 'system', '2020-03-12 14:01:59.544', 'system', '2020-03-12 14:01:59.544', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('68239db3-673f-409a-a6c9-81a6b263f950', '06', 'EXPENSES', true, 'EXPENSES', NULL, false, 'system', '2020-03-12 14:01:59.549', 'system', '2020-03-12 14:01:59.549', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('9a4d1bc3-55d9-451a-936c-1ae3ae349537', '01', 'OPERATING EXPENSES', true, 'FS_PROFITLOSS_OPEX', '68239db3-673f-409a-a6c9-81a6b263f950', false, 'system', '2020-03-12 14:01:59.553', 'system', '2020-03-12 14:01:59.553', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('641fe9a9-cfc2-40b4-818b-4e7d245bc69c', '01', 'SALARIES AND WAGES', true, '', '9a4d1bc3-55d9-451a-936c-1ae3ae349537', false, 'system', '2020-03-12 14:01:59.557', 'system', '2020-03-12 14:01:59.557', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('8a6e6528-b333-4529-ab2a-4bccfe5b16bf', '001', 'MANAGEMENT & SUPERVISION', false, 'COST_CENTER', '641fe9a9-cfc2-40b4-818b-4e7d245bc69c', false, 'system', '2020-03-12 14:01:59.560', 'system', '2020-03-12 14:01:59.560', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c2f66d3f-355f-4777-81a8-a5af59b2cf3a', '002', 'REGISTERED NURSES', false, 'COST_CENTER', '641fe9a9-cfc2-40b4-818b-4e7d245bc69c', false, 'system', '2020-03-12 14:01:59.564', 'system', '2020-03-12 14:01:59.564', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('71ae1144-7d52-4ad0-bb49-04ac583ee580', '003', 'AIDES, ORDERLIES AND ATTENDANTS', false, 'COST_CENTER', '641fe9a9-cfc2-40b4-818b-4e7d245bc69c', false, 'system', '2020-03-12 14:01:59.569', 'system', '2020-03-12 14:01:59.569', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c024953f-2caa-4f7b-878a-01b5cb231f37', '004', 'OTHER EMPLOYEE CLASSIFICATIONS', false, 'COST_CENTER', '641fe9a9-cfc2-40b4-818b-4e7d245bc69c', false, 'system', '2020-03-12 14:01:59.573', 'system', '2020-03-12 14:01:59.573', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('6e4953ae-4eeb-497e-a7ac-2effda4503f7', '02', 'EMPLOYEE BENEFITS', true, '', '9a4d1bc3-55d9-451a-936c-1ae3ae349537', false, 'system', '2020-03-12 14:01:59.577', 'system', '2020-03-12 14:01:59.577', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('0d59f153-e9a9-45de-86c9-ddf2ee04caae', '001', '13TH MONTH PAY', false, 'COST_CENTER', '6e4953ae-4eeb-497e-a7ac-2effda4503f7', false, 'system', '2020-03-12 14:01:59.581', 'system', '2020-03-12 14:01:59.581', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('08dd424c-9d29-46d1-9571-9099f3a20fa9', '002', 'INCENTIVES & BONUSES', false, 'COST_CENTER', '6e4953ae-4eeb-497e-a7ac-2effda4503f7', false, 'system', '2020-03-12 14:01:59.586', 'system', '2020-03-12 14:01:59.586', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('17abbe5f-dbb9-45a9-b40e-31d3269b5ebf', '003', 'DI-MINIMIS BENEFITS', false, 'COST_CENTER', '6e4953ae-4eeb-497e-a7ac-2effda4503f7', false, 'system', '2020-03-12 14:01:59.591', 'system', '2020-03-12 14:01:59.591', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('ddf00e1a-8b83-4086-8c5d-470acf1ddbd0', '004', 'SSS EMPLOYER''S CONTRIBUTIONS', false, 'COST_CENTER', '6e4953ae-4eeb-497e-a7ac-2effda4503f7', false, 'system', '2020-03-12 14:01:59.596', 'system', '2020-03-12 14:01:59.596', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('56a57a7f-770b-47d3-913e-77d76fe5e450', '005', 'PHILHEALTH EMPLOYER''S CONTRIBUTIONS', false, 'COST_CENTER', '6e4953ae-4eeb-497e-a7ac-2effda4503f7', false, 'system', '2020-03-12 14:01:59.600', 'system', '2020-03-12 14:01:59.600', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('0feee4a9-b05d-4c30-927d-a64b2f283cc1', '006', 'HDMF EMPLOYER''S CONTRIBUTIONS', false, 'COST_CENTER', '6e4953ae-4eeb-497e-a7ac-2effda4503f7', false, 'system', '2020-03-12 14:01:59.605', 'system', '2020-03-12 14:01:59.605', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('015bc5d1-9a55-48ea-b885-091e5b13a1a7', '007', 'OTHER EMPLOYEE BENEFITS', false, 'COST_CENTER', '6e4953ae-4eeb-497e-a7ac-2effda4503f7', false, 'system', '2020-03-12 14:01:59.609', 'system', '2020-03-12 14:01:59.609', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('30eb0fde-2e0a-4f00-a4ee-c4b06b400aba', '03', 'PROFESSIONAL FEES', true, '', '9a4d1bc3-55d9-451a-936c-1ae3ae349537', false, 'system', '2020-03-12 14:01:59.613', 'system', '2020-03-12 14:01:59.613', NULL);
INSERT INTO accounting.chart_of_accounts
(id, account_code, description, category, tags, parent, deprecated, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('2c52204d-3e65-4efd-bdb5-12ffa43a6b42', '001', 'MEDICAL PHYSICIANS', false, 'COST_CENTER', '30eb0fde-2e0a-4f00-a4ee-c4b06b400aba', false, 'system', '2020-03-12 14:01:59.617', 'system', '2020-03-12 14:01:59.617', NULL);
