alter table ancillary.services drop column availabe;

alter table ancillary.services
	add hidden boolean default false;

