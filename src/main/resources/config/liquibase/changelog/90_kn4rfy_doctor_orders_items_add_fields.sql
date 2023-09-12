alter table pms.doctor_order_items
  add column action varchar(20),
  add column medication_type varchar(20),
  add column frequency varchar(20),
  add column route varchar(20),
  add column additive text,
  add column additional_instructions text;
