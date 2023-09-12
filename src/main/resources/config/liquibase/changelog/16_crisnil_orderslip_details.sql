create table IF NOT EXISTS ancillary.orderslips_details
(
  id          uuid,
  field_name  varchar,
  field_value varchar,
  service     uuid,
  orderslip   uuid
    constraint fk_orderslips_details_orderslip
      references orderslips
      on update cascade on delete cascade
);
