CREATE TABLE doh.create_rvs_account (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	"hfhudname" varchar,
	"fhudaddress" varchar,
	"regcode" varchar,
	"provcode" varchar,
	"ctymuncode" varchar,
	"bgycode" varchar,
	"fhudtelno1" varchar,
	"fhudtelno2" varchar,
	"fhudfaxno" varchar,
	"fhudemail" varchar,
	"headlname" varchar,
	"headfname" varchar,
	"headmname" varchar,
	"accessKey" varchar,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);