INSERT INTO accounting.integration
(id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain", integration_group)
VALUES('8dc24583-0094-40c0-b2a4-108f25bddc94'::uuid, 'CONSIGNMENT RECEIVING', NULL, 'CONSIGNMENT_RECEIVING', 6, 'whinacay', '2023-05-26 04:06:02.328', 'admin', '2023-05-26 04:06:02.328', NULL, 'com.hisd3.hismk2.domain.inventory.ReceivingReport', '367c34c6-74c3-4ff9-a705-dfc1719357f1'::uuid);

INSERT INTO accounting.integration
(id, description, flag_property, flag_value, order_priority, created_by, created_date, last_modified_by, last_modified_date, deleted, "domain", integration_group)
VALUES('576c39c0-83c7-4064-a49e-73305616d140'::uuid, 'FIX ASSET RECEIVING', NULL, 'FIX_ASSET_RECEIVING', 6, 'whinacay', '2023-05-26 04:06:02.328', 'admin', '2023-05-26 04:06:02.328', NULL, 'com.hisd3.hismk2.domain.inventory.ReceivingReport', '367c34c6-74c3-4ff9-a705-dfc1719357f1'::uuid);

UPDATE inventory.purchase_order set is_fixed_asset = false  where is_fixed_asset is null;
