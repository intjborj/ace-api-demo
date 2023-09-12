alter table ancillary.orderslip_item
	add batch_num varchar;

alter table ancillary.orderslip_item
  RENAME COLUMN timestarted TO time_started;

alter table ancillary.orderslip_item
  RENAME COLUMN timecompleted TO time_completed;
