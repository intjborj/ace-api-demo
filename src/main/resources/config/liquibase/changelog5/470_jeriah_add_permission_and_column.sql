alter table hospital_configuration.operational_configuration
	ADD COLUMN "allow_phic_onempty_doh_icd" boolean default false,
	ADD COLUMN "allow_rvs_onempty_doh_ops" boolean default false;

