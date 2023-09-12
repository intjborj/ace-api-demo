alter table pms.transfers
	drop column if exists "active" CASCADE;

alter table pms.cases
	drop column if exists "active_transfer" CASCADE;