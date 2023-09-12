CREATE TABLE "accounting"."balance_sheet_saved_amounts" (
	"id" uuid NOT NULL,
	"year" varchar,
	"retained_earnings" numeric,
	"net_profit" numeric,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"deleted" bool,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)