
CREATE SCHEMA cashiering ;


CREATE TABLE cashiering.cashierterminals (
                                             id uuid NOT NULL DEFAULT uuid_generate_v4(),
                                             terminal_id varchar NULL,
                                             remarks varchar NULL,
                                             ipaddresses varchar NULL,
                                             CONSTRAINT cashierterminals_pkey PRIMARY KEY (id),
                                             CONSTRAINT un_ipaddress UNIQUE (ipaddresses)
);


ALTER TABLE cashiering.cashierterminals
    ADD COLUMN "created_by" varchar(50) COLLATE "pg_catalog"."default",
    ADD COLUMN "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
    ADD COLUMN "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP;
