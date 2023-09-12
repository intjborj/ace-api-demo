CREATE INDEX "docnum_idx" ON "accounting"."header_ledger" USING BTREE ("docnum");

CREATE INDEX "particulars_idx" ON "accounting"."header_ledger" USING BTREE ("particulars");