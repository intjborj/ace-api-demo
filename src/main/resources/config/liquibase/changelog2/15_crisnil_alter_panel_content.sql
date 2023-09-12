alter table ancillary.panel_content drop constraint services;

alter table ancillary.panel_content
	add constraint services
		foreign key (parent) references ancillary.services
			on update cascade on delete restrict;
