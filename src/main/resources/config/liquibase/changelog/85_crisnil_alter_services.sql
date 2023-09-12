alter table ancillary.services
	add column "created_by" varchar(50);

alter table ancillary.services
	add column "created_date" timestamp(6) default CURRENT_TIMESTAMP;

alter table ancillary.services
	add column "last_modified_by" varchar(50);

alter table ancillary.services
	add column "last_modified_date" timestamp(6) default CURRENT_TIMESTAMP;
