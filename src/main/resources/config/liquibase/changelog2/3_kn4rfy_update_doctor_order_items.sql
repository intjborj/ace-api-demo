alter table "pms"."doctor_order_items"
  alter column status set data type varchar(20);

alter table "pms"."medications"
  add column volume varchar(20),
  add column flow_rate varchar(20),
  add column additive uuid;