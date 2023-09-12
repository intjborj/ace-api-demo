ALTER TABLE "cashiering"."payment_tracker_details" ADD COLUMN "collection_detail" uuid;

ALTER TABLE "cashiering"."payment_tracker_details" ADD FOREIGN KEY ("collection_detail")
    REFERENCES "cashiering"."collection_detail" ("id") ON UPDATE CASCADE ON DELETE SET NULL;