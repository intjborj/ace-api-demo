alter table pms.medications alter column frequency type varchar (250);
alter table pms.medications alter column dose type varchar (250);
alter table pms.medications alter column route type varchar (250);
alter table pms.medications alter column remarks type text;
alter table pms.medications alter column "type" type varchar (250);

alter table pms.administrations alter column "action" type varchar (250);
alter table pms.administrations alter column "dose" type varchar (250);