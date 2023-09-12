ALTER TABLE cashiering.collection_detail DROP CONSTRAINT collection_detail_fk_1;
ALTER TABLE cashiering.collection_detail ADD CONSTRAINT collection_detail_fk_1 FOREIGN KEY (bank) REFERENCES accounting.bankaccounts(id) ON DELETE RESTRICT ON UPDATE CASCADE;
