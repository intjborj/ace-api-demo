create table pms.neuro_vital_signs
(
  id                      uuid not null
    constraint neuro_vital_signs_pkey
      primary key,
  entry_datetime          timestamp    default CURRENT_TIMESTAMP,
  gcs_eye_response        varchar(100),
  gcs_verbal_response     varchar(100),
  gcs_best_motor_response varchar(100),
  gcs_total               varchar(15),
  es_right_size           varchar(15),
  es_right_reaction       varchar(15),
  es_left_size            varchar(15),
  es_left_reaction        varchar(15),
  ls_arms_left            varchar(100),
  ls_arms_right           varchar(100),
  ls_legs_left            varchar(100),
  ls_legs_right           varchar(100),
  employee                uuid,
  deleted                 boolean,
  "case"                  uuid
    constraint fk_neuro_vital_signs_cases
      references pms.cases
      on update cascade on delete restrict,
  created_by              varchar(50),
  created_date            timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by        varchar(50),
  last_modified_date      timestamp(6) default CURRENT_TIMESTAMP
);
