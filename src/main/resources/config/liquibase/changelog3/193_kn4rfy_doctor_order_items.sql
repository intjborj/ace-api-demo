alter table pms.doctor_order_items
  add column attending_physician uuid,
  add column managing_physician uuid;
