alter table ancillary.orderslips
	add requesting_physician uuid;

alter table ancillary.orderslips
	add requesting_physician_name varchar;

alter table ancillary.orderslips
	add notes varchar;