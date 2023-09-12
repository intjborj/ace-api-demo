INSERT INTO accounting.integration (id,description,flag_property,flag_value,order_priority,created_by,created_date,last_modified_by,last_modified_date,deleted,"domain") VALUES 
('e31d8985-d627-4bab-a009-242fd00d4e49','CHECK_CLEARING',NULL,'CHECK_CLEARING',8004,'admin','2020-11-16 15:42:05.129','admin','2020-11-16 15:42:43.931',NULL,'com.hisd3.hismk2.domain.IntegrationTemplate')
,('8d08395c-b123-49f0-80f9-5fb714c05ce8','CARD_CLEARING',NULL,'CARD_CLEARING',8005,'admin','2020-11-16 15:43:02.982','admin','2020-11-16 15:43:16.356',NULL,'com.hisd3.hismk2.domain.IntegrationTemplate')
,('92740789-8cb3-412c-b7ab-4ebe7b878d62','IP-VENTILATOR',NULL,'IPD-VENTILATOR',3006,'jescabusa','2020-10-27 12:57:37.509','jescabusa','2020-10-28 09:02:05.805',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('de0ad660-03de-44a5-9cba-1bebbbad50f2','OPD-SERVICES',NULL,'OPD_SERVICES',1001,'admin','2020-10-22 02:53:31.079','admin','2020-10-22 02:53:58.096',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('6e0e0c8a-0f49-4fd8-b47d-f99fbffa3ea5','OPD-SUPPLIES',NULL,'OPD_SUPPLIES',1002,'admin','2020-10-22 03:32:03.576','admin','2020-10-22 03:32:10.536',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('2ea5d9af-430e-40ce-af85-b20e2b02274c','OPD-MEDS',NULL,'OPD_MEDS',1003,'jescabusa','2020-10-27 05:05:32.726','jescabusa','2020-10-27 05:06:12.522',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('fe1a713a-9832-4b62-9fe2-522987f67889','OPD-ROOM',NULL,'OPD_ROOM',1004,'jescabusa','2020-10-27 06:42:17.871','jescabusa','2020-10-27 06:42:46.566',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('32755f3b-cf13-4f77-b862-a70a121e4c98','OPD-OXYGEN',NULL,'OPD_OXYGEN',1005,'jescabusa','2020-10-27 07:21:34.498','jescabusa','2020-10-27 07:21:53.295',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('a143aa51-aee4-4b06-afeb-8f70cedd84ea','OPD-VENTILATOR',NULL,'OPD-VENTILATOR',1006,'jescabusa','2020-10-27 11:24:28.344','jescabusa','2020-10-27 11:24:47.942',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('ffb5ace8-375b-4b99-96b6-801f3583efd2','IP-OXYGEN',NULL,'IP_OXYGEN',3005,'jescabusa','2020-10-27 12:42:23.955','jescabusa','2020-10-28 09:09:34.195',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
;
INSERT INTO accounting.integration (id,description,flag_property,flag_value,order_priority,created_by,created_date,last_modified_by,last_modified_date,deleted,"domain") VALUES 
('925cfcf6-af0d-4dee-a610-819a0487e2cd','IP-SERVICES',NULL,'IP_SERVICES',3001,'jescabusa','2020-10-27 11:42:44.445','jescabusa','2020-10-27 11:53:37.883',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('d2c3c526-707c-48ad-b6a5-2e1994afd08a','IP-SUPPLIES',NULL,'IP_SUPPLIES',3002,'jescabusa','2020-10-27 11:49:33.836','jescabusa','2020-10-27 11:53:48.333',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('3d6a192b-61ce-456a-9bee-0c9c2d3fe0c7','ER-SERVICES',NULL,'ER_SERVICES',2001,'jescabusa','2020-10-28 09:29:10.657','jescabusa','2020-10-28 09:29:20.006',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('ee00f992-971f-4b3f-8f18-a4724ccf43db','IP-MEDS',NULL,'IP_MEDS',3003,'jescabusa','2020-10-27 12:28:14.515','jescabusa','2020-10-27 12:29:23.530',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('301c5cd8-d6bd-411a-a56d-0d463a56a923','IP-ROOM',NULL,'IP_ROOM',3004,'jescabusa','2020-10-27 12:39:10.905','jescabusa','2020-10-27 12:39:22.955',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('e5a6405b-d025-49cb-a2cb-800d9aa68848','ER-SUPPLIES',NULL,'ER_SUPPLIES',2002,'jescabusa','2020-10-28 09:33:02.284','jescabusa','2020-10-28 09:34:23.659',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('f6eaaad5-f56f-4e15-83f6-a166c0d756dc','ER-MEDS',NULL,'ER_MEDS',2003,'jescabusa','2020-10-28 09:51:53.417','jescabusa','2020-10-28 09:52:15.626',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('c42f0e6d-072e-42ff-a620-58fd990552a2','ER-ROOM',NULL,'ER_ROOM',2004,'jescabusa','2020-10-28 10:02:00.741','jescabusa','2020-10-28 10:02:12.640',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('2e85c54b-3ef5-4aa2-9d3d-1dbd6cdf17b5','ER-VENTILATOR',NULL,'ERD-VENTILATOR',2006,'jescabusa','2020-10-28 14:41:57.759','jescabusa','2020-10-28 14:42:23.447',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('5e453ac6-d8e5-4585-9b39-be1d6fad865f','ER-OXYGEN',NULL,'ER_OXYGEN',2005,'jescabusa','2020-10-28 14:37:42.427','jescabusa','2020-10-28 15:23:18.171',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
;
INSERT INTO accounting.integration (id,description,flag_property,flag_value,order_priority,created_by,created_date,last_modified_by,last_modified_date,deleted,"domain") VALUES 
('d03fb592-9739-4eed-ad14-3e2e2ea5b31f','OTC-MEDS',NULL,'OTC_MEDS',4001,'jescabusa','2020-10-30 08:40:36.127','jescabusa','2020-10-30 08:41:18.152',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('14bd14dd-756d-441a-ae17-2096e86e1efa','OTC-NONVAT-MEDS',NULL,'OTC_NONVAT_MEDS',5001,'jescabusa','2020-10-30 09:27:37.671','jescabusa','2020-10-30 09:28:12.475',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('1fd8a457-897c-4499-8111-276aa31ac773','OTC-NONVAT-SUPPLIES',NULL,'OTC_NONVAT_SUPPLIES',5002,'jescabusa','2020-10-30 09:35:59.846','jescabusa','2020-10-30 09:36:23.525',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('68188296-e3de-4ae2-8b32-a61dedbd35f9','OTC-SUPPLIES',NULL,'OTC_SUPPLIES',4002,'jescabusa','2020-10-30 09:21:56.859','jescabusa','2020-10-30 10:39:13.865',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('0d1abba3-7d08-412b-b1cb-f156b794829d','HOSPITAL_PAYMENTS',NULL,'HOSPITAL_PAYMENTS',6001,'admin','2020-11-01 03:43:56.571','admin','2020-11-02 08:48:22.809',NULL,'com.hisd3.hismk2.domain.cashiering.PaymentTracker')
,('401ad4ae-f4fe-4cee-972d-b37d9aadae0c','DISCOUNTS_DEDUCT',NULL,'DISCOUNTS_DEDUCT',7001,'admin','2020-11-04 05:42:00.911','admin','2020-11-04 05:42:28.898',NULL,'com.hisd3.hismk2.domain.billing.BillingItem')
,('61b7147b-d2d3-4e70-acc9-a5b9fec45042','REAPPLICATION_OF_PAYMENTS',NULL,'REAPPLICATION_OF_PAYMENTS',8001,'admin','2020-11-06 01:33:38.866','admin','2020-11-06 01:33:56.729',NULL,'com.hisd3.hismk2.domain.IntegrationTemplate')
,('5c698100-d3da-4252-9395-47c4e42aa826','MISC_PAYMENTS',NULL,'MISC_PAYMENTS',6002,'admin','2020-11-06 12:51:08.900','admin','2020-11-06 12:51:19.840',NULL,'com.hisd3.hismk2.domain.cashiering.PaymentTracker')
,('3aec03db-bfb6-4df4-83a0-93354f1d88ce','REAPPLY_OR',NULL,'REAPPLY_OR',8002,'admin','2020-11-06 21:27:18.222','admin','2020-11-06 21:27:27.918',NULL,'com.hisd3.hismk2.domain.IntegrationTemplate')
,('536e28a7-c2ac-4217-b917-4e6e5426b893','COLLECTION_DEPOSIT',NULL,'COLLECTION_DEPOSIT',8003,'admin','2020-11-09 10:14:30.522','admin','2020-11-09 10:14:47.500',NULL,'com.hisd3.hismk2.domain.IntegrationTemplate')
;