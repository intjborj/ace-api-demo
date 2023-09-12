alter table pms.medications
  add column doctors_order_item_id uuid;
--     constraint fk_medications_doctor_order_items
--       references pms.doctor_order_items
--       on update cascade on delete restrict;

alter table ancillary.orderslips
  add column doctors_order_item_id uuid;
--     constraint fk_orderslips_doctor_order_items
--       references pms.doctor_order_items
--       on update cascade on delete restrict;
