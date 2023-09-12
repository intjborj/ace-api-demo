alter table ancillary.integration_config alter column demo_mode set default true;

alter table ancillary.integration_config alter column nas_location type varchar(50) using nas_location::varchar(50);

alter table ancillary.integration_config drop column carestream_url;

alter table ancillary.integration_config drop column watched_directory;

alter table ancillary.integration_config alter column enable_integration set default false;

alter table ancillary.integration_config
	add ldap_url varchar(50);

alter table ancillary.integration_config alter column middleware_ip type varchar(10) using middleware_ip::varchar(10);

alter table ancillary.integration_config
	add port int default 389;

alter table ancillary.integration_config
	add admin_dn varchar(100) default E'cn=LDAPAdministrator,dc=college,dc=org,dc=in';

alter table ancillary.integration_config drop column adt_port;

alter table ancillary.integration_config drop column orm_port;

alter table ancillary.integration_config
	add password varchar(100);

