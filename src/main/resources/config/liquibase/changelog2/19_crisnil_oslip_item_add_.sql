alter table ancillary.orderslip_item
	add item_type varchar(20) default 'DIAGNOSTICS';

alter table ancillary.orderslip_item
	add member_of varchar(20) default 'REGULAR';
