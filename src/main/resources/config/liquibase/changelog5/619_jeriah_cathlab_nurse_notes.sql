create table pms.cathlab_nurse_notes
    (
      id                 uuid not null
        constraint cathlab_nurse_notes_pkey
          primary key,
      entry_datetime     timestamp    default CURRENT_TIMESTAMP,
      summary           varchar,
      comment           varchar,
      employee           uuid,
      "case"             uuid
        constraint fk_cathlab_nurse_notes_cases
          references pms.cases
          on update cascade on delete restrict,
      deleted            boolean,
      created_by         varchar(50),
      created_date       timestamp(6) default CURRENT_TIMESTAMP,
      last_modified_by   varchar(50),
      last_modified_date timestamp(6) default CURRENT_TIMESTAMP
    );