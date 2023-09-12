

--
--ALTER TABLE billing.investors
--    ADD COLUMN barangay                         varchar,
--    ADD COLUMN city                             varchar,
--    ADD COLUMN province                         varchar,
--    ADD COLUMN country                          varchar,
--    ADD COLUMN zip_code                         varchar,
--    ADD COLUMN gender                           varchar,
--    ADD COLUMN civil_status                     varchar,
--    ADD COLUMN place_of_birth                   varchar,
--    ADD COLUMN email_address                    varchar,
--    ADD COLUMN present_employer                 varchar,
--    ADD COLUMN educational_attainment           varchar,
--    ADD COLUMN profession                       varchar,
--    ADD COLUMN office_address                   varchar,
--    ADD COLUMN contact_numbers                  jsonb,
--    ADD COLUMN identifications                  jsonb,
--    ADD COLUMN referred_by                      uuid constraint fk_investors_employee_id
--                                                    references hrm.employees(id)
--                                                    on update cascade on delete restrict;
--
--

CREATE TABLE "billing"."investor_subscriptions" (
    id                                                uuid NOT NULL PRIMARY KEY,
    investor                                          uuid NOT NULL
                                                           constraint fk_investor_subscription_investor_id
                                                           references billing.investors(id)
                                                           on update cascade on delete restrict,
    shares	                                          int,
    subscription_price	                              int,
    par_value	                                      int,

    created_by                                        varchar(50),
    created_date                                      timestamp DEFAULT now(),
    last_modified_by                                  varchar(50),
    last_modified_date                                timestamp DEFAULT now(),
    deleted                                           bool
);

ALTER TABLE billing.investor_payment_ledger
    ADD COLUMN subscription                  uuid constraint fk_investor_payment_ledger_subscription_id
                                                  references billing.investor_subscriptions(id)
                                                  on update cascade on delete restrict;

alter table billing.investor_payment_ledger alter column payment_tracker drop not null;


--
--CREATE TABLE "billing"."investor_payment_ledger" (
--    id                                                uuid NOT NULL PRIMARY KEY,
--    investor                                          uuid NOT NULL
--                                                           constraint fk_payment_ledger_investor_id
--                                                           references billing.investors(id)
--                                                           on update cascade on delete restrict,
--    shift                                             uuid NOT NULL
--                                                           constraint fk_payment_ledger_shifting_id
--                                                           references cashiering.shifting(id)
--                                                           on update cascade on delete restrict,
--    date_received                                     timestamp,
--    payment_mode                                      varchar,
--    check_date                                        timestamp,
--    particular                                        varchar,
--    amount                                            numeric(15,2),
--    date_deposited                                    timestamp,
--    date_cleared                                      timestamp,
--    subscription_number                               varchar,
--    reference_number                                  varchar,
--    acquiring_bank                                    varchar,
--    depository_bank                                   varchar,
--    remarks                                           varchar,
--
--    created_by                                        varchar(50),
--    created_date                                      timestamp DEFAULT now(),
--    last_modified_by                                  varchar(50),
--    last_modified_date                                timestamp DEFAULT now(),
--    deleted                                           bool
--);
