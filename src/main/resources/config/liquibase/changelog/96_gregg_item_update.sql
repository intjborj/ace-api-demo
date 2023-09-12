ALTER TABLE "inventory"."item" RENAME COLUMN "consignment_item" TO "consignment";
ALTER TABLE "inventory"."item"
	ADD COLUMN "item_code" varchar,
	ADD COLUMN "item_group" uuid,
	ADD COLUMN "item_category" uuid,
	ADD COLUMN "item_generics" uuid,
	ADD COLUMN "unit_of_purchase" uuid,
	ADD COLUMN "unit_of_usage" uuid,
	ADD COLUMN "item_conversion" numeric,
	ADD COLUMN "item_demand_qty" numeric,
	ADD COLUMN "discountable" bool,
	ADD COLUMN "production" bool,
	ADD COLUMN "reagent" bool,
	ADD COLUMN "item_dfa" bool;
ALTER TABLE "inventory"."item" ADD CONSTRAINT "fk_item_group" FOREIGN KEY ("item_group") REFERENCES "inventory"."item_groups" ("id"),
	ADD CONSTRAINT "fk_item_category" FOREIGN KEY ("item_category") REFERENCES "inventory"."item_categories" ("id"),
	ADD CONSTRAINT "fk_item_generics" FOREIGN KEY ("item_generics") REFERENCES "inventory"."generics" ("id"),
	ADD CONSTRAINT "fk_unit_of_purchase" FOREIGN KEY ("unit_of_purchase") REFERENCES "inventory"."unit_measurements" ("id"),
	ADD CONSTRAINT "fk_unit_of_usage" FOREIGN KEY ("unit_of_usage") REFERENCES "inventory"."unit_measurements" ("id");