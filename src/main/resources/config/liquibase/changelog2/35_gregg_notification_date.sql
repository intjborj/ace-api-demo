ALTER TABLE "public"."notifications"
	ADD COLUMN "date_notified" timestamp NULL,
	ADD COLUMN "date_seen" timestamp NULL;