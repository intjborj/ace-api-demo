ALTER TABLE "billing"."medsupply_cashbasis" ADD COLUMN "type" varchar;

ALTER TABLE "billing"."medsupply_cashbasis" ADD FOREIGN KEY ("billing") REFERENCES "billing"."billing" ("id") ON UPDATE CASCADE ON DELETE CASCADE;