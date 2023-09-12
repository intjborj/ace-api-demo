CREATE TABLE cashiering.payment_target (
                                           id uuid NULL,
                                           chart_of_account uuid NULL,
                                           amount numeric(15,2) NULL,
                                           payment_tracker uuid NULL,
                                           CONSTRAINT payment_target_pk PRIMARY KEY (id),
                                           CONSTRAINT payment_target_fk FOREIGN KEY (chart_of_account)
                                               REFERENCES accounting.chart_of_accounts(id) ON DELETE SET NULL ON UPDATE CASCADE,
                                           CONSTRAINT payment_target_fk_1 FOREIGN KEY (payment_tracker)
                                               REFERENCES cashiering.payment_tracker(id) ON DELETE SET NULL ON UPDATE CASCADE
);

ALTER TABLE "cashiering"."payment_target"
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;

CREATE TABLE cashiering.payment_target_details
(
    id             uuid    NULL DEFAULT uuid_generate_v4(),
    field_name     varchar NULL,
    field_value    varchar NULL,
    payment_target uuid    NULL,
    CONSTRAINT fk_payment_target_details_payment_target FOREIGN KEY (payment_target) REFERENCES cashiering.payment_target (id) ON UPDATE CASCADE ON DELETE CASCADE
)
