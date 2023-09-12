INSERT INTO t_authority (name)
SELECT 'ROLE_BILLING'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'ROLE_BILLING'
);

INSERT INTO t_authority (name)
SELECT 'ROLE_CASHIERING'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'ROLE_CASHIERING'
);

INSERT INTO t_authority (name)
SELECT 'ROLE_RECEIVABLE'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'ROLE_RECEIVABLE'
);

INSERT INTO t_authority (name)
SELECT 'ROLE_PAYABLE'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'ROLE_PAYABLE'
);

INSERT INTO t_authority (name)
SELECT 'ROLE_ACC_SETUP'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'ROLE_ACC_SETUP'
);

INSERT INTO t_authority (name)
SELECT 'ROLE_TRANS_JOURNAL'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'ROLE_TRANS_JOURNAL'
);


INSERT INTO t_authority (name)
SELECT 'ROLE_FIN_REPORT'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'ROLE_FIN_REPORT'
);

INSERT INTO t_authority (name)
SELECT 'ROLE_INVESTOR'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_authority WHERE name = 'ROLE_INVESTOR'
);

