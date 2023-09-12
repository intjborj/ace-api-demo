CREATE TABLE philhealth.claims
(
    id uuid NOT NULL,
    batch_no character varying,
    ticket_no character varying,
    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool,
    PRIMARY KEY (id)
);

CREATE TABLE philhealth.claim_items
(
    id uuid NOT NULL,
    "case" uuid,
    claim uuid,
    tracking_no character varying,
    total_amt_actual numeric,
    total_amt_claimed numeric,
    claim_type character varying,
    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool,
    PRIMARY KEY (id)
);