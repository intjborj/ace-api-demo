ALTER TABLE "billing"."billing"
  ADD COLUMN "patient" uuid,
  ADD COLUMN "case" uuid;