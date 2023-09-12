create schema pms;

create table pms.patients
(
  id                             uuid not null
    constraint patients_pkey
      primary key,
  patient_no                     varchar,
  last_name                      varchar,
  first_name                     varchar,
  middle_name                    varchar,
  name_suffix                    varchar,
  address                        varchar,
  country                        varchar,
  state_province                 varchar,
  city_municipality              varchar,
  barangay                       varchar,
  gender                         varchar,
  dob                            date,
  allergies                      varchar,
  father                         varchar,
  mother                         varchar,
  father_occupation              varchar,
  mother_occupation              varchar,
  emergency_contact_name         varchar,
  emergency_contact_address      varchar,
  emergency_contact_relationship varchar,
  emergency_contact_no           varchar,
  guarantor_name                 varchar,
  guarantor_address              varchar,
  guarantor_relationship         varchar,
  guarantor_contact_no           varchar,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);