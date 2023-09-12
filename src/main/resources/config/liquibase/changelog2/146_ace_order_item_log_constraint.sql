ALTER TABLE "pms"."doctor_order_item_logs"
    ADD COLUMN "doctor_order_item" uuid,
    ADD CONSTRAINT "fk_logs_items"
    FOREIGN KEY ("doctor_order_item")
    REFERENCES "pms"."doctor_order_items" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;