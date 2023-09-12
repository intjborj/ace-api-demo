CREATE TABLE doh.staffing_pattern_others (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
    "parent" int,
    "professiondesignation" varchar,
    "specialtyboardcertified" int,
    "fulltime40permanent" int,
    "fulltime40contractual" int,
    "parttimepermanent" int,
    "parttimecontractual" int,
    "activerotatingaffiliate" int,
    "outsoured" int,
    "reportingyear" int,



	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);