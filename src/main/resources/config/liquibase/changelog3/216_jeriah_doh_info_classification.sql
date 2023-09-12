CREATE SCHEMA "doh";

CREATE TABLE doh.classifications (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	"service_capability"      varchar,
    "general"                 varchar,
	"specialty"               varchar,
	"specify_specialty"       varchar,
	"trauma_capability"       varchar,
	"nature_of_ownership"     varchar,
	"government"              varchar,
	"nationals"               varchar,
	"locals"                  varchar,
	"private"                 varchar,
	"reporting_year"          int4,
	"ownership_others"        varchar,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);