CREATE TABLE cashiering.cheque_encashment(
    id uuid not null primary key,
    record_no varchar,
    terminal uuid,
    shift uuid,
    cheque_no varchar,
    cheque_date date,
    bank uuid,
    remarks text,
    amount numeric,
    cleared bool,
    cleared_date timestamp(6) default CURRENT_TIMESTAMP,
    denied bool,
    denied_date timestamp(6) default CURRENT_TIMESTAMP,
    collection_detail UUID,
    transaction_date timestamp(6) default CURRENT_TIMESTAMP,
    returned_shift_id uuid,
    returned_date timestamp(6) default CURRENT_TIMESTAMP,
    returned_personnel uuid,
    returned_remarks varchar,
    posted_ledger uuid,
    return_posted_ledger uuid,

    "created_by" varchar(50) COLLATE "default",
    "created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
    "last_modified_by" varchar(50) COLLATE "default",
    "last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cashiering.cheque_encashment_suppfiles
(
    id                      uuid      NOT NULL,
    cheque_encashment       uuid      NULL,
    reference_no            varchar   NULL,
    description             varchar   NULL,
    attachment              bytea     NULL,
    created_by              varchar   NULL,
    created_date       timestamp NULL,
    last_modified_by   varchar   NULL,
    last_modified_date timestamp NULL,
    filename           varchar   NULL,
    CONSTRAINT pk_specialty PRIMARY KEY (id),
    CONSTRAINT fk_ce_supportingfiles FOREIGN KEY (cheque_encashment) REFERENCES cashiering.cheque_encashment (id)
);



