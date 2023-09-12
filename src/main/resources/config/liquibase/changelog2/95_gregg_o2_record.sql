CREATE TABLE "pms"."o2_administration" (
	"id" uuid,
	"item" uuid,
	"case" uuid,
	"flowrate" uuid,
	"start" timestamp NULL,
	"end" timestamp NULL,
	"calculated_minutes" numeric,
	    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        boolean
)
WITH (OIDS=FALSE);