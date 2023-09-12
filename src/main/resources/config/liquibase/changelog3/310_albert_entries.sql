
ALTER TABLE accounting.ledger DROP CONSTRAINT fk_ledger_header;
ALTER TABLE accounting.ledger ADD CONSTRAINT fk_ledger_header FOREIGN KEY (header) REFERENCES accounting.header_ledger(id) ON UPDATE CASCADE ON DELETE CASCADE;