ALTER TABLE "bms"."rooms"
    ADD COLUMN "department" uuid;

ALTER TABLE "bms"."rooms"
    ADD CONSTRAINT "fk_rooms_department"
    FOREIGN KEY ("department")
    REFERENCES "public"."departments" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;