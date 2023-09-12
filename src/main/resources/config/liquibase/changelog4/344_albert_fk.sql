ALTER TABLE cashiering.collection_detail DROP CONSTRAINT collection_detail_fk;
ALTER TABLE cashiering.collection_detail ADD CONSTRAINT collection_detail_fk FOREIGN KEY (collection) REFERENCES cashiering.collection(id);
