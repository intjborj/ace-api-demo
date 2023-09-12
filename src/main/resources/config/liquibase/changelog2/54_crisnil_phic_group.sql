create table philhealth.phic_group
(
	id                  uuid not null primary key,
	phic_group_name     varchar(100),
	phic_group_description  varchar(150),
	created_by         varchar(50),
    created_date       timestamp(6) default now(),
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default now(),
    deleted            boolean
);
