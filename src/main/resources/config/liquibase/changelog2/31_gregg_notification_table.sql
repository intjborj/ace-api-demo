CREATE TABLE "public"."notifications" (
	"id" uuid NOT NULL,
	"from" uuid,
	"to" uuid,
	"department" uuid,
	"message" varchar,
	"title" varchar,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);