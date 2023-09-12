DROP TABLE  if EXISTS doh.classifications;
CREATE TABLE doh.classification (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	"service_capability"      int,
    "general"                 int,
	"specialty"               int,
	"specialty_Specify"       varchar,
	"trauma_capability"       int,
	"nature_of_ownership"     int,
	"government"              int,
	"nationals"               int,
	"locals"                  int,
	"private"                 int,
	"reporting_year"          int4,
	"ownership_others"        varchar,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);