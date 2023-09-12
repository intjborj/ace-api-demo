ALTER TABLE cashiering.shifting ADD CONSTRAINT cdctr_shifting_fk
    FOREIGN KEY (cdctr) REFERENCES cashiering.cdctr(id) ON DELETE SET NULL ON UPDATE CASCADE;
