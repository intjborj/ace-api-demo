create table accounting.account_receivable_items_details
(
  id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
  account_receivable_items       uuid,
  field_name                     varchar,
  field_value                    varchar,

  CONSTRAINT fk_ar_group_account_receivable_items FOREIGN KEY (account_receivable_items) REFERENCES accounting.account_receivable_items(id) ON UPDATE CASCADE ON DELETE CASCADE

);


CREATE INDEX idx_accaritm ON accounting.account_receivable_items_details USING btree (account_receivable_items);
CREATE INDEX idx_acaritmfieldname ON accounting.ar_group USING btree (field_name);
CREATE INDEX idx_acaritmfieldvalue ON accounting.ar_group USING btree (field_value);