CREATE TABLE cashiering.cdctr (
                                  id uuid NOT NULL,
                                  totalcollection numeric(15,2) NULL,
                                  receivedby varchar NULL,
                                  received_datetime varchar NULL,
                                  recno varchar NULL,
                                  CONSTRAINT cdctr_pk PRIMARY KEY (id)
);


ALTER TABLE "cashiering"."cdctr"
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;
