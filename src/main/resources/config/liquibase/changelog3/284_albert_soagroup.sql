CREATE TABLE billing.soa_groupings (
                                       id uuid NULL,
                                       billing_id uuid NULL,
                                       group_name varchar NULL,
                                       CONSTRAINT soa_groupings_pk PRIMARY KEY (id),
                                       CONSTRAINT soa_groupings_fk FOREIGN KEY (billing_id) REFERENCES billing.billing(id) ON DELETE CASCADE ON UPDATE CASCADE
);



ALTER TABLE "billing"."soa_groupings"
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;




ALTER TABLE billing.billing_item ADD soa_group_id uuid NULL;
ALTER TABLE billing.billing_item ADD CONSTRAINT billing_item_soagroup_fk FOREIGN KEY (soa_group_id) REFERENCES billing.soa_groupings(id)  ON DELETE SET NULL ON UPDATE CASCADE;
