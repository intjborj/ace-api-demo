INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT 'bc746113-bc30-4cd0-b7ca-d95f9ab62609'::uuid, '01000', 'LAND', true, false, 'admin', '2022-09-09 21:37:13.516', '', '2022-09-09 21:37:13.516'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = 'bc746113-bc30-4cd0-b7ca-d95f9ab62609'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT 'bfabe3c0-6d2d-4a5e-8509-1bad743cc11a'::uuid, '02000', 'LAND IMPROVEMENTS', true, false, 'admin', '2022-09-09 21:47:28.681', '', '2022-09-09 21:47:28.681'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = 'bfabe3c0-6d2d-4a5e-8509-1bad743cc11a'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '9e1c10e7-6b6c-49fe-8d61-d5d2fdb54528'::uuid, '03000', 'OTHER LAND IMPROVEMENT', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '9e1c10e7-6b6c-49fe-8d61-d5d2fdb54528'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '45189f8d-b284-414a-8fd6-c0ff3250a53b'::uuid, '04010', 'HOSPITAL BUILDING', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '45189f8d-b284-414a-8fd6-c0ff3250a53b'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '67649b5c-3f8e-4c19-bc6b-f63e437864c5'::uuid, '04020', 'HOSPITAL BUILDING ELEVATOR', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '67649b5c-3f8e-4c19-bc6b-f63e437864c5'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '4343ffac-5e1a-478b-8426-32916882a7cc'::uuid, '04030', 'HOSPITAL BUILDING POWER HOUSE', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '4343ffac-5e1a-478b-8426-32916882a7cc'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '2a4408c8-5114-4524-9f76-d79d33235b9f'::uuid, '04040', 'HOSPITAL BUILDING WATERPUMP', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '2a4408c8-5114-4524-9f76-d79d33235b9f'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT 'b3ff5f06-64ec-497d-ad77-2b3bd92c9d34'::uuid, '04050', 'HOSPITAL BUILDING LAUNDRY HOUSE', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = 'b3ff5f06-64ec-497d-ad77-2b3bd92c9d34'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '3f94819e-3f35-4520-8d06-de65dae1de4a'::uuid, '05000', 'LEASEHOLD IMPROVEMENTS', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '3f94819e-3f35-4520-8d06-de65dae1de4a'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '9cbba392-7488-4c21-9be3-36d6b1c88cb8'::uuid, '06000', 'HOSPITAL EQUIPMENT', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '9cbba392-7488-4c21-9be3-36d6b1c88cb8'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '2814401e-b28c-4f52-9878-510f1d6dcf2d'::uuid, '07000', 'MEDICAL EQUIPMENT', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '2814401e-b28c-4f52-9878-510f1d6dcf2d'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT 'fc624bf4-c8ae-4ddb-8d30-7cc086668ca1'::uuid, '08000', 'TRANSPORTATION/MOBILE EQUIPMENT', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = 'fc624bf4-c8ae-4ddb-8d30-7cc086668ca1'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT 'db3a696a-4a02-4368-971e-7f3a33198cdb'::uuid, '09000', 'OFFICE EQUIPMENT', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = 'db3a696a-4a02-4368-971e-7f3a33198cdb'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT 'b53c9482-e520-4462-a9ef-b6598c8b602c'::uuid, '10000', 'HOSPITAL FURNITURES & FIXTURES', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = 'b53c9482-e520-4462-a9ef-b6598c8b602c'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '33659a82-3abd-48d1-b85d-aa40b6c90399'::uuid, '11000', 'OFFICE FURNITURES & FIXTURES', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '33659a82-3abd-48d1-b85d-aa40b6c90399'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT 'de4ed815-0fea-4fe8-8d4c-200a477cd9d6'::uuid, '12000', 'TOOLS & KITCHEN EQUIPMENT-DIETARY', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = 'de4ed815-0fea-4fe8-8d4c-200a477cd9d6'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '00e56d85-dde6-4569-a083-759a28cb476e'::uuid, '13000', 'TOOLS & KITCHEN EQUIPMENT-DINING', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '00e56d85-dde6-4569-a083-759a28cb476e'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '3ef77f10-fdcf-4dc3-b116-d083b67b956f'::uuid, '14000', 'TOOLS & EQUIPMENT-BIOMED ENGINEERING', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '3ef77f10-fdcf-4dc3-b116-d083b67b956f'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT 'ab5527c5-a427-4ff7-81b4-db801ccc7d3a'::uuid, '15000', 'TOOLS & EQUIPMENT-GENERAL SERVICES', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = 'ab5527c5-a427-4ff7-81b4-db801ccc7d3a'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT 'beb65e3a-5267-45ae-9e08-e57dab151baa'::uuid, '16000', 'ELECTRICAL AND INSTALLATION EQUIPMENT', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = 'beb65e3a-5267-45ae-9e08-e57dab151baa'
  );
INSERT INTO fixed_assets.fixed_asset_category (id, category_code, category_description, is_active, deleted, created_by, created_date, last_modified_by, last_modified_date)
SELECT '7c8f5b37-d241-436b-8970-bc378b559704'::uuid, '17000', 'TOOLS & KITCHEN EQUIPMENT-OPERATING ROOM', true, false, 'admin', '2022-09-09 21:59:14.883', '', '2022-09-09 21:59:14.883'
WHERE NOT EXISTS(
    SELECT 1 FROM fixed_assets.fixed_asset_category WHERE id = '7c8f5b37-d241-436b-8970-bc378b559704'
  );
