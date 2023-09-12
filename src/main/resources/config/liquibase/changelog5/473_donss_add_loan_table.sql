CREATE TABLE IF NOT EXISTS accounting.loans(
    id         uuid not null primary key,
    loan_no  varchar(50),
    reference_no varchar(50),
    start_date date,
    account_no uuid,
    compound_type varchar(50),
    interest_rate numeric,
    loan_period integer,
    number_of_payments integer,
    loan_amount numeric,
    loan_payment numeric,
    total_interest numeric,
    total_cost_of_loan numeric,
    posted_ledger uuid,

    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
    deleted            boolean
);

Create TABLE IF NOT EXISTS accounting.loan_accounts(
    id  uuid not null primary key,
    account_no varchar,
    account_name varchar,
    bank uuid,

    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
    deleted            boolean
);

CREATE TABLE IF NOT EXISTS accounting.loan_amortization(
    id         uuid not null primary key,
    loan uuid,
    order_no  integer,
    record_no  varchar(50),
    reference_no varchar(50),
    payment_date date,
    beginning_balance numeric,
    payment numeric,
    principal numeric,
    interest numeric,
    ending_balance numeric,
    posted_ledger uuid,

    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
    deleted            boolean
);
