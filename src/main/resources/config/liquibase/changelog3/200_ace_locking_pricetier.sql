alter table billing.item_price_controls
	add column "locked" boolean default false;

alter table billing.service_price_controls
	add column "locked" boolean default false;
