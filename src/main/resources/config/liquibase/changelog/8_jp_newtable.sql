CREATE TABLE "inventory"."receiving_report"
(
  "id"                   uuid NOT NULL,
  "supplier"             varchar(255),
  "ref_no"               varchar,
  "receiving_department" varchar(255),
  "ref_qlty"             varchar,
  "qlty_inspection_date" timestamp,
  PRIMARY KEY ("id")
)
;

CREATE TABLE "inventory"."receiving_report_items"
(
  "id"               uuid NOT NULL,
  "receiving_report" uuid,
  "barcode"          varchar,
  "description"      varchar(255),
  "ref_no"           varchar,
  "po_qty"           numeric,
  "qty_delivered"    numeric,
  "po_balance_qty"   numeric,
  "amt"              numeric,
  PRIMARY KEY ("id")
)
;