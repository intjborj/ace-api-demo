alter table ancillary.services alter column servicename type varchar(50) using servicename::varchar(50);

alter table ancillary.services alter column description type varchar(50) using description::varchar(50);

alter table ancillary.services alter column category type varchar(50) using category::varchar(50);

alter table ancillary.services alter column notes type varchar(50) using notes::varchar(50);

alter table ancillary.services alter column service_code type varchar(20) using service_code::varchar(20);

alter table ancillary.services alter column process_code type varchar(20) using process_code::varchar(20);

alter table ancillary.services
	add section varchar(20);