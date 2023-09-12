ALTER TABLE "pms"."cases"
    DROP COLUMN "transfer_HCI";

ALTER TABLE "pms"."cases"
    ADD COLUMN "transfer_hci" varchar(255);