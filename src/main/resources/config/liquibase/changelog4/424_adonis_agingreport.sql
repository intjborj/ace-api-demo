CREATE TYPE accounting.aragingreportperpatient AS
(
    billing_date date ,
    soa_no varchar,
    arno  character varying ,
    patient varchar,
    due_date date,
    current_days numeric(15, 2),
    day_1_to_30 numeric(15, 2),
    day_31_to_60 numeric(15, 2),
    day_61_to_90 numeric(15, 2),
    day_91_to_120 numeric(15, 2),
    day_older numeric(15, 2),
    total numeric(15, 2)
);