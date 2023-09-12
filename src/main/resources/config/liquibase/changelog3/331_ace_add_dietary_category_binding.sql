ALTER TABLE "dietary"."diets"
  ADD COLUMN "dietary_category" uuid;

ALTER TABLE "dietary"."diets"
  ADD CONSTRAINT "fk_diets_dietary_category"
  FOREIGN KEY ("dietary_category")
  REFERENCES "dietary"."diet_categories" ("id")
  ON UPDATE CASCADE ON DELETE RESTRICT;