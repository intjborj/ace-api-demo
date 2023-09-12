CREATE TABLE pms.obgyn_history
(
    id uuid DEFAULT uuid_generate_v4 (),
    "case" uuid NOT NULL,
    menarche numeric,
    gravida numeric,
    parturition numeric,
    abortion numeric,
    living numeric,
    menopause numeric,
    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool,
    PRIMARY KEY (id),
    FOREIGN KEY ("case")
        REFERENCES pms.cases (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
);

