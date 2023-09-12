ALTER TABLE billing.billing_item ADD postedledger uuid NULL;
ALTER TABLE billing.billing_item ADD canceledref uuid NULL;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE billing.billing_item_details (
	id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
	field_name varchar NULL,
	field_value varchar NULL,
	billingitem uuid NULL,
	CONSTRAINT fk_billing_item_details_billingitem FOREIGN KEY (billingitem) REFERENCES billing.billing_item(id) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX idx_bdbiitem ON billing.billing_item_details USING btree (billingitem);
CREATE INDEX idx_bdfieldname ON billing.billing_item_details USING btree (field_name);
CREATE INDEX idx_bdfieldvalue ON billing.billing_item_details USING btree (field_value);