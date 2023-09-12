drop table if exists "inventory"."document_types";
create table inventory.document_types
(
  id                            uuid not null primary key,
  document_code                 varchar,
  document_desc                 varchar,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool
);

INSERT INTO inventory.document_types VALUES(
  '254a07d3-e33a-491c-943e-b3fe6792c5fc',
  'SRR',
  'STOCK RECIEVING',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '85dbf47b-fd3d-419b-b2b0-1d5621ba7f38',
  'MI',
  'MATERIAL ISSUES',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '19c0c388-7e85-4abf-aa13-cdafecf8dc8c',
  'CS',
  'CHARGESLIP',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  'd12f0de2-cb65-42ab-bcdb-881ebce57045',
  'STO',
  'STOCKTRANSFER OUT',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '7250e64a-de1b-4015-80fb-e15f9f6762ab',
  'STI',
  'STOCKTRANSFER IN',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '0702930b-a1ec-4f64-be6a-7f656ac4c300',
  'RCS',
  'PATIENT RETURN',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '56461ef7-5162-46ac-8fbb-0ab2bdcc2746',
  'RTS',
  'RETURN TO SUPPLIER',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  'c17eb9d9-c0bd-432b-987e-b3c89edecab8',
  'CSI',
  'CASH SALES INVOICE',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '0f3c2b76-445a-4f78-a256-21656bd62872',
  'EX',
  'EXPENSE',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '0caab388-e53b-4e94-b2ea-f8cc47df6431',
  'BEG',
  'BEGINING BALANCE',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '57a37685-a20c-415e-9837-e2f138fef667',
  'BEG',
  'BEGINING BALANCE',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '7b94c82f-081a-4578-82c2-f7343852fcf3',
  'SRR (FG)',
  'STOCK RECIEVING (FG)',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  'af7dc429-8352-4f09-b58c-26a0a490881c',
  'EP',
  'EMERGENCY PURCHASE',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '4f88d8d7-ecce-4538-a97b-88884b1e106e',
  'ADJ',
  'QUANTITY ADJUSTMENT',
  'system'
);

INSERT INTO inventory.document_types VALUES(
  '37683c86-3038-4207-baf0-b51456fd7037',
  'PHY',
  'PHYSICAL COUNT',
  'system'
);




