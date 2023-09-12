alter table ancillary.orderslip_item
	add constraint orderslip_item_pk
		primary key (id);

alter table ancillary.orderslip_item
	add constraint orderslip_item_orderslips__fk
		foreign key (orderslip) references ancillary.orderslips
			on update cascade on delete restrict;
