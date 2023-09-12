create table ancillary.orderSlip_item
(
	id uuid,
	orderSlip uuid,
	service uuid,
	timeStarted timestamp,
	timeCompleted timestamp,
	posted boolean,
	status varchar,
	created_by varchar,
  created_date timestamp,
  last_modified_by varchar,
  last_modified_date timestamp,
  deleted boolean
);
