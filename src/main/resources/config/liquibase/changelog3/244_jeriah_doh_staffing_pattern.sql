CREATE TABLE doh.staffing_pattern (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	 "parent" int,
      "profession_designation" varchar,
      "specialty_board_certified" int,
      "fulltime_40permanent" int,
      "fulltime_40contractual" int,
      "parttime_permanent" int,
      "parttime_contractual" int,
      "active_rotating_affiliate" int,
      "out_soured" int,
      "reporting_year" int,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);