CREATE TABLE accounting.integration (
                                        id uuid NULL,
                                        description varchar NULL,
                                        flag_property varchar NULL,
                                        flag_value varchar NULL,
                                        order_priority int,
                                        CONSTRAINT integration_pk PRIMARY KEY (id)
);


ALTER TABLE accounting.integration
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;

ALTER TABLE accounting.integration ADD COLUMN "deleted" bool;


CREATE TABLE accounting.integration_items (
                                        id uuid NULL,
                                        journal_account jsonb NULL,
                                        disabled_property varchar NULL,
                                        disabled_value varchar NULL,
                                        value_property varchar NULL,
                                        integration uuid,
                                        CONSTRAINT integration_items_pk PRIMARY KEY (id),
                                        CONSTRAINT fk_integration_items_integration FOREIGN KEY (integration) REFERENCES accounting.integration(id) ON UPDATE CASCADE ON DELETE CASCADE
);


ALTER TABLE accounting.integration_items
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;

ALTER TABLE accounting.integration_items ADD COLUMN "deleted" bool;


CREATE TABLE accounting.integration_items_details (
                                           id uuid NULL DEFAULT uuid_generate_v4(),
                                           field_name varchar NULL,
                                           field_value varchar NULL,
                                           integration_item uuid NULL,
                                           CONSTRAINT fk_integration_details_1 FOREIGN KEY (integration_item) REFERENCES accounting.integration_items(id) ON UPDATE CASCADE ON DELETE CASCADE
);