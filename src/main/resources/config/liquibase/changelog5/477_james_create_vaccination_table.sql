create table IF NOT EXISTS pms.patient_vaccinations
(
	id uuid,
	patient uuid,
	case_id uuid,
	vaccine_date DATE,
	vaccination varchar(100),
	dose_frequency varchar(100),
	health_facility varchar(100),
	administered_by varchar(100),
	created_by varchar(50) NULL,

  created_date timestamp NULL DEFAULT now(),
  last_modified_by varchar(50) NULL,
  last_modified_date timestamp NULL DEFAULT now(),
  deleted            boolean
);