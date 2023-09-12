
CREATE TABLE cashiering.collection (
                                       id uuid NULL,
                                       transaction_date_time timestamp NULL,
                                       remarks varchar NULL,
                                       CONSTRAINT collection_pk PRIMARY KEY (id)
);

ALTER TABLE "cashiering"."collection"
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;


CREATE TABLE cashiering.collection_detail (
                                              id uuid NULL,
                                              collection varchar NULL,
                                              amount numeric(15,2) NULL,
                                              bank uuid NULL,
                                              deposit_reference_no varchar,
                                              "type" varchar NULL,
                                              CONSTRAINT collection_detail_pk PRIMARY KEY (id),
                                              CONSTRAINT collection_detail_fk FOREIGN KEY (id) REFERENCES cashiering.collection(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                              CONSTRAINT collection_detail_fk_1 FOREIGN KEY (id) REFERENCES accounting.bankaccounts(id) ON DELETE RESTRICT ON UPDATE CASCADE
);



ALTER TABLE "cashiering"."collection_detail"
       ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;
