
ALTER TABLE ancillary.orderslip_item
    ADD COLUMN discontinued_datetime TIMESTAMP,
    ADD COLUMN discontinued_by uuid constraint fk_orderslip_item_employee_id references hrm.employees(id) on update cascade on delete restrict;
