
ALTER TABLE cashiering.collection_detail ADD terminal uuid NULL;
ALTER TABLE cashiering.collection_detail ADD CONSTRAINT collection_detail_fk2
    FOREIGN KEY (terminal) REFERENCES cashiering.cashierterminals(id) ON DELETE RESTRICT ON UPDATE CASCADE;
