CREATE TABLE cashiering.applied_or (
                                           id uuid NOT NULL,
                                           billing_item_id uuid NULL,
                                           amount numeric(15,2) NULL,
                                           payment_tracker uuid NULL,
                                           created_by varchar NULL,
                                           created_date timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                           last_modified_by varchar NULL,
                                           last_modified_date timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                           CONSTRAINT apply_or_pk PRIMARY KEY (id),
                                           CONSTRAINT apply_or_fk_1 FOREIGN KEY (payment_tracker) REFERENCES cashiering.payment_tracker(id) ON UPDATE CASCADE ON DELETE CASCADE,
                                           CONSTRAINT apply_or_fk_2 FOREIGN KEY (billing_item_id) REFERENCES billing.billing_item(id) ON UPDATE CASCADE ON DELETE SET NULL
);
