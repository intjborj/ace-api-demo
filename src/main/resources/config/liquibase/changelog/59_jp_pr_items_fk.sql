ALTER TABLE "inventory"."purchase_request_items"
  ADD CONSTRAINT "fk_purchase_request" FOREIGN KEY ("ref_pr") REFERENCES "inventory"."purchase_request" ("id") ON DELETE RESTRICT ON UPDATE NO ACTION;