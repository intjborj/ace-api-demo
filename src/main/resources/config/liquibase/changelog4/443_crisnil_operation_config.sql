alter table hospital_configuration.operational_configuration
	add column "auto_lock_ipd" boolean default false;

	alter table hospital_configuration.operational_configuration
  add column auto_lock_opd boolean default false;

  alter table hospital_configuration.operational_configuration
  add column auto_lock_er boolean default false;
