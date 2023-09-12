CREATE TABLE billing.package (
                                 id uuid NULL,
                                 description varchar NULL,
                                 code varchar NULL,
                                 remarks varchar NULL,
                                 company_account_discount uuid NULL,
                                 company_account_subsidy uuid NULL,
                                 package_price numeric(15,2) NULL,
                                 CONSTRAINT newtable_pk PRIMARY KEY (id),
                                 CONSTRAINT package_fk FOREIGN KEY (company_account_discount) REFERENCES billing.companyaccounts(id) ON DELETE SET NULL ON UPDATE CASCADE,
                                 CONSTRAINT package_fk_1 FOREIGN KEY (company_account_subsidy) REFERENCES billing.companyaccounts(id) ON DELETE SET NULL ON UPDATE CASCADE
);


CREATE TABLE billing.package_items (
                                         id uuid NULL,
                                         service_id uuid NULL,
                                         item_id uuid NULL,
                                         active boolean NULL,
                                         selling_price numeric(15,2) NULL,
                                         package uuid NULL,
                                         CONSTRAINT package_details_pk PRIMARY KEY (id),
                                         CONSTRAINT package_details_fk FOREIGN KEY (package) REFERENCES billing.package(id) ON DELETE CASCADE ON UPDATE CASCADE
);


ALTER TABLE "billing"."package"
    ADD COLUMN "created_by" varchar(50)  NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp(6)  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar(50) COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp(6) NULL DEFAULT current_timestamp;


ALTER TABLE "billing"."package_items"
    ADD COLUMN "created_by" varchar(50)  NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp(6)  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar(50) COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp(6) NULL DEFAULT current_timestamp;