INSERT INTO inventory.document_types
(id, document_code, document_desc, created_by, created_date, last_modified_by, last_modified_date, deleted)
VALUES('c71a1f34-4358-4d6d-b504-488f1fcd4c31'::uuid, 'MPS', 'MATERIAL PRODUCTION', 'system', '2020-04-07 11:05:32.102', NULL, '2020-04-07 11:05:32.102', NULL);

UPDATE inventory.document_types
SET document_code='MPO', document_desc='MATERIAL PRODUCTION', created_by='system', created_date='2020-04-07 11:05:32.768', last_modified_by=NULL, last_modified_date='2020-04-07 11:05:32.768', deleted=NULL
WHERE id='27d236bb-c023-44dc-beac-18ddfe1daf79'::uuid;
