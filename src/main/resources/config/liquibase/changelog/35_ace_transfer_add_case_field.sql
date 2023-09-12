ALTER TABLE "pms"."transfers"
    ADD COLUMN "case" uuid;

ALTER TABLE "pms"."transfers"
    ADD CONSTRAINT "fk_transfers_case"
    FOREIGN KEY ("case")
    REFERENCES "pms"."cases" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;