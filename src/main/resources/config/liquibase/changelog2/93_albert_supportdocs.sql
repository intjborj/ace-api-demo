CREATE TABLE billing.supportingfiles
(
    id                 uuid      NOT NULL,
    billing_item       uuid      NULL,
    reference_no       varchar   NULL,
    remarks            varchar   NULL,
    attachment         bytea     NULL,
    created_by         varchar   NULL,
    created_date       timestamp NULL,
    last_modified_by   varchar   NULL,
    last_modified_date timestamp NULL,
    filename           varchar   NULL,
    CONSTRAINT pk_specialty PRIMARY KEY (id),
    CONSTRAINT fk_supportingfiles FOREIGN KEY (billing_item) REFERENCES billing.billing_item (id)
);



