alter table ancillary.diagnostic_results
	add constraint diagnostic_results_pk
		primary key (id);

alter table ancillary.diagnostic_results
	add constraint diagnostic_results_orderslip_item__fk
		foreign key (orderslip_item) references ancillary.orderslip_item
			on update cascade on delete restrict;
