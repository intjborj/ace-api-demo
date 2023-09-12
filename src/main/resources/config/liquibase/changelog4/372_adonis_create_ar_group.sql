create table accounting.ar_group
(
  id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
  account_receivable          uuid NULL,
  field_name                  varchar NULL,
  field_value                 uuid NULL,

  CONSTRAINT fk_ar_group_account_receivable FOREIGN KEY (account_receivable) REFERENCES accounting.account_receivable(id) ON UPDATE CASCADE ON DELETE CASCADE

);

CREATE INDEX idx_accar ON accounting.ar_group USING btree (account_receivable);
CREATE INDEX idx_acarfieldname ON accounting.ar_group USING btree (field_name);
CREATE INDEX idx_acarfieldvalue ON accounting.ar_group USING btree (field_value);