alter table "pms"."doctor_order_items"
  add column item uuid,
  add column start_time timestamp(6) default null,
  add column end_time timestamp(6) default null;
