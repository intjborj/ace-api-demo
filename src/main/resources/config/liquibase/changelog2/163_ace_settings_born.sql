CREATE TABLE hospital_configuration.default_settings (
	id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
	default_credit_limit numeric,
	allow_do_wo_pn bool
);