CREATE SCHEMA "clinic";

CREATE TABLE clinic.clinics (
	id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
	clinic_name varchar,
	clinic_address varchar,
	clinic_contact varchar,
	contact_contact_name varchar
);