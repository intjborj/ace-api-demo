alter table ancillary.orderslips
  drop column doctors_order_item_id;

alter table ancillary.orderslip_item
  rename column doctors_order to doctors_order_item;
