CREATE TABLE doh.revenues (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	"amountfromdoh" decimal,
	"amountfromlgu" decimal,
	"amountfromdonor" decimal,
	"amountfromprivateorg" decimal,
	"amountfromphilhealth" decimal,
	"amountfrompatient" decimal,
	"amountfromreimbursement" decimal,
	"amountfromothersources" decimal,
	"grandtotal" decimal,
	"reportingyear" int,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);