alter table ancillary.orderslip_item
	add schedule_date timestamp;


alter table ancillary.orderslips
	add requesting_physician_prc varchar(50);


