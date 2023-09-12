alter table accounting.chart_of_accounts
    add column account_type varchar,
    add column fs_type varchar,
    add column normal_side varchar,
    add column is_contra bool default false;
