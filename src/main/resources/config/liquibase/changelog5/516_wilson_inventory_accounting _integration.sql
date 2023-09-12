INSERT INTO accounting.integration
(id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain")
VALUES('629c479f-38aa-488d-9f8a-c2b8d63375f5'::uuid, 'MATERIAL_PRODUCTION', NULL, 'MATERIAL_PRODUCTION', 9, 'whinacay', '2022-09-29 06:54:15.860', 'whinacay', '2022-09-29 06:55:30.340', NULL, 'com.hisd3.hismk2.domain.inventory.MaterialProduction') ON CONFLICT (ID) DO NOTHING;

INSERT INTO accounting.integration
(id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain")
VALUES('ea8b9996-cd32-4220-9758-b0e1bff1bf41'::uuid, 'RETURN SUPPLIER', NULL, 'RETURN_TO_SUPPLIER', 8, 'whinacay', '2022-10-08 16:42:21.985', 'whinacay', '2022-10-08 16:43:05.730', NULL, 'com.hisd3.hismk2.domain.inventory.ReturnSupplier') ON CONFLICT (ID) DO NOTHING;

INSERT INTO accounting.integration
(id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain")
VALUES('7fd3b7ca-d3a4-4476-86ad-0c4a76060326'::uuid, 'STOCK EXPENSE', NULL, 'ITEM_EXPENSE', 7, 'whinacay', '2022-10-17 06:29:25.340', 'whinacay', '2022-10-17 06:30:05.480', NULL, 'com.hisd3.hismk2.domain.inventory.DepartmentStockIssue') ON CONFLICT (ID) DO NOTHING;

INSERT INTO accounting.integration
(id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain")
VALUES('081e8581-be17-46a2-a980-54af69032771'::uuid, 'STOCK ISSUANCE', NULL, 'ITEM_ISSUANCE', 6, 'whinacay', '2022-10-17 06:28:53.607', 'whinacay', '2022-10-17 06:30:33.230', NULL, 'com.hisd3.hismk2.domain.inventory.DepartmentStockIssue') ON CONFLICT (ID) DO NOTHING;

INSERT INTO accounting.integration
(id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain")
VALUES('1fce1580-3e7d-46e5-b5d4-115b9be77148'::uuid, 'QTY_ADJUSTMENT', NULL, 'QTY_ADJUSTMENT', 5, 'whinacay', '2022-10-20 06:38:27.386', 'whinacay', '2022-10-20 06:38:58.707', NULL, 'com.hisd3.hismk2.domain.inventory.QuantityAdjustment') ON CONFLICT (ID) DO NOTHING;
