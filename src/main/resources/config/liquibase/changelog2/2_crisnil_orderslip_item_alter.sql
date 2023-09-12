alter table ancillary.orderslip_item
	add reader uuid;

alter table ancillary.orderslip_item
	add waved decimal;

alter table ancillary.orderslip_item
	add billing_item uuid;