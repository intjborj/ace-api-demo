ALTER TABLE cashiering.cdctr ADD collection uuid NULL;
ALTER TABLE cashiering.cdctr ADD CONSTRAINT cdctr_fk FOREIGN KEY (collection) REFERENCES cashiering.collection(id) ON DELETE RESTRICT ON UPDATE CASCADE;
