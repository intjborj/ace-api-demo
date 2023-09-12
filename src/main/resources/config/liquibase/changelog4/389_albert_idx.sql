CREATE INDEX "revenue_tag_idx" ON "public"."departments" USING BTREE ("revenue_tag");

UPDATE "public"."departments" SET "revenue_tag" = 'MEDICINES' WHERE "id" = '8d91200c-f4b6-4bd8-81ed-7b63e6dbeca3';

UPDATE "public"."departments" SET "revenue_tag" = 'SUPPLIES' WHERE "id" = 'fe8a6736-a88e-498c-93b0-fd2970622d38';