CREATE TABLE billing.billingitems_amountdetails (
                                                    id uuid NULL DEFAULT uuid_generate_v4(),
                                                    billingitemsid varchar NULL,
                                                    amount numeric(15,2) NULL,
                                                    billingitem uuid NULL,
                                                    CONSTRAINT fk_billingitems_amountdetails_billingitem
                                                        FOREIGN KEY (billingitem) REFERENCES billing.billing_item(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX dx_biamt_billingitem ON billing.billingitems_amountdetails USING btree (billingitem);
CREATE INDEX idx_biamt_billingitemsid ON billing.billingitems_amountdetails USING btree (billingitemsid);