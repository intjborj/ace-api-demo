CREATE TABLE "hospital_configuration"."hosp_operation_hai" (
  "id" uuid NOT NULL,
  "numhai" float,
  "numdischarges" float,
  "infectionrate" float,
  "patientnumvap" float,
  "totalventilatordays" float,
  "resultvap" float,
  "patientnumbsi" float,
  "totalnumcentralline" float,
  "resultbsi" float,
  "patientnumuti" float,
  "totalcatheterdays" float,
  "resultuti" float,
  "numssi" float,
  "totalproceduresdone" float,
  "resultssi" float,
  "reportingyear" int4,


  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);