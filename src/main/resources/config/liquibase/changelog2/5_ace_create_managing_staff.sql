alter table pms.managing_physicians
    add column position varchar,
    drop column is_ap;