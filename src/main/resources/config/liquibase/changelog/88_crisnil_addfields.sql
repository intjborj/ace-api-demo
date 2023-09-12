alter table ancillary.services
	add column "package" boolean default FALSE;

alter table ancillary.services
	add column "markup" numeric;

alter table ancillary.services
	add column "readers_fee" numeric;
