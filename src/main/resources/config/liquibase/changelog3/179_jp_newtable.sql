CREATE TABLE hospital_configuration.cost_estimation
(
    id uuid NOT NULL,
    description varchar,
    diagnosis varchar,
    patient uuid,
    attending_physician uuid,
    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);

CREATE TABLE hospital_configuration.cost_estimation_item
(
    id uuid NOT NULL,
    type varchar,
    cost_estimation uuid,
    ref_id varchar,
    amount     numeric,
    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);
