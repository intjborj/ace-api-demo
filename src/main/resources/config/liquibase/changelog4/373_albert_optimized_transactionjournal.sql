CREATE INDEX "entity_name_idx" ON "accounting"."header_ledger" USING BTREE ("entity_name");

CREATE INDEX "invoice_soa_reference_idx" ON "accounting"."header_ledger" USING BTREE ("invoice_soa_reference");