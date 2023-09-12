alter table pms.cases
    drop time_of_birth,
    add column time_of_birth timestamp(6);