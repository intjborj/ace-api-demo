create table pms.vaccination
(
	id uuid,
	patient uuid,
	vaccianted boolean default false,
	full_vaccinated boolean default false,
	brand varchar(100),
	other varchar(100),
	first_dose timestamp,
	second_dose timestamp,
	created_by varchar(50) NULL,
  created_date timestamp NULL DEFAULT now(),
  last_modified_by varchar(50) NULL,
  last_modified_date timestamp NULL DEFAULT now(),
  deleted            boolean
);

create unique index vaccination_id_uindex
	on pms.vaccination (id);

alter table pms.vaccination
	add constraint vaccination_pk
		primary key (id);

