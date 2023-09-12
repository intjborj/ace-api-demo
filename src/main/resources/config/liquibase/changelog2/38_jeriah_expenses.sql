CREATE TABLE "hospital_configuration"."expenses" (
  "id" uuid NOT NULL,
  "salarieswages" decimal,
  "employeebenefits" decimal,
  "allowances" decimal,
  "totalps" decimal,
  "totalamountmedicine" decimal,
  "totalamountmedicalsupplies" decimal,
  "totalamountutilities" decimal,
  "totalamountnonmedicalservice" decimal,
  "totalmooe" decimal,
  "amountinfrastructure" decimal,
  "amountequipment" decimal,
  "totalco" decimal,
  "grandtotal" decimal,
  "reportingyear" int4,


  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);