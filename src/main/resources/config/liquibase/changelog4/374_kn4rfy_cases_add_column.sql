alter table pms.cases
  add column investor bool default false,
  add column investor_id varchar(50);

