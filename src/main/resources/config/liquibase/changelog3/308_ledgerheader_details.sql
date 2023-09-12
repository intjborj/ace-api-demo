CREATE TABLE accounting.header_ledger_details (
                                                      id uuid NULL DEFAULT uuid_generate_v4(),
                                                      field_name varchar NULL,
                                                      field_value varchar NULL,
                                                      header_ledger uuid NULL,
                                                      CONSTRAINT header_ledger_details_1 FOREIGN KEY (header_ledger) REFERENCES accounting.header_ledger(id) ON UPDATE CASCADE ON DELETE CASCADE
);

