create table pms.vaccination_shots
(
	id uuid not null,
	patient uuid,
	vaccine_brand uuid,
	vaccine_name varchar(50),
	dose varchar(50),
	date_administered timestamp,
	administered_by varchar(50),
	created_by varchar(50) NULL,
  created_date timestamp NULL DEFAULT now(),
  last_modified_by varchar(50) NULL,
  last_modified_date timestamp NULL DEFAULT now(),
  covid_shot            boolean,
  deleted            boolean
);

create unique index vaccination_shots_id_uindex
	on pms.vaccination_shots (id);

