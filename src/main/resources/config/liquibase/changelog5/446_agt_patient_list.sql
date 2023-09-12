DROP TABLE IF EXISTS appointment.patients;
CREATE TABLE appointment.patients (
	id                  uuid NOT NULL,
	patient_no          varchar(50) NULL,
	his_patient         uuid NULL,
	last_name           varchar(50) NULL,
	first_name          varchar(50) NULL,
	middle_name         varchar(50) NULL,
	name_suffix         varchar(50) NULL,
	address             text NULL,
	gender              varchar(50) NULL,
	dob                 date NULL,
	pob                 varchar NULL,
	nationality         varchar(300) NULL,
	religion            varchar NULL,
	citizenship         varchar(255) NULL,
	civil_status        varchar(255) NULL,

	name_of_spouse      varchar(255) NULL,
	spouse_occupation   varchar NULL,

	father              varchar(300) NULL,
	mother              varchar(300) NULL,
	father_occupation   varchar(300) NULL,
	mother_occupation   varchar(300) NULL,

	contact_no          varchar NULL,
	other_contact       varchar NULL,
	email_address       varchar(300) NULL,

    country             varchar(300) NULL,
	state_province      varchar(300) NULL,
	city_municipality   varchar(300) NULL,
	barangay            varchar(300) NULL,
	zip_code            varchar(5) NULL,

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT CURRENT_TIMESTAMP,

	CONSTRAINT patients_pkey PRIMARY KEY (id)
);