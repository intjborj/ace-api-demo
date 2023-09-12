create table pms.cases
(
  id                      uuid not null
    constraint cases_pkey
      primary key,
  case_no                 varchar,
  status                  varchar      default 'ACTIVE',
  service_type            varchar,
  accommodation_type      varchar,
  registry_type           varchar,
  entry_datetime          timestamp    default CURRENT_TIMESTAMP,
  admission_datetime      timestamp without time zone,
  discharged_datetime     timestamp without time zone,
  may_go_home_datetime    timestamp without time zone,
  admitting_diagnosis     varchar,
  discharge_diagnosis     varchar,
  pre_op_diagnosis        varchar,
  post_op_diagnosis       varchar,
  history_present_illness varchar,
  past_medical_history    varchar,
  surgical_procedure      varchar,
  informant               varchar,
  informant_relation      varchar,
  informant_address       varchar,
  patient                 uuid
    constraint fk_cases_patient
      references pms.patients
      on update cascade on delete restrict,
  created_by              varchar(50),
  created_date            timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by        varchar(50),
  last_modified_date      timestamp(6) default CURRENT_TIMESTAMP
);