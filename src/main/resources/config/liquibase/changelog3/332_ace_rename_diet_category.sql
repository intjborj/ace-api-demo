ALTER TABLE "dietary"."diet_categories"
  RENAME COLUMN "item_group_code" TO "diet_category_code";

ALTER TABLE "dietary"."diet_categories"
RENAME COLUMN "item_group_description" TO "diet_category_description";