CREATE TABLE pms.doctor_order_item_logs (
	id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
	action varchar,
    entry_datetime timestamp(6) default CURRENT_TIMESTAMP,
	employee uuid
    constraint fk_logs_employee references pms.doctor_order_items on update cascade on delete restrict
);