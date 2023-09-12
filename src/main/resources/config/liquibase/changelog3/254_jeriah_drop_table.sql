CREATE TABLE doh.hosp_quality_management (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	"quality_mgmttype"          int,
    "description"               varchar,
	"certifying_body"           varchar,
	"phil_health_accredition"   int,
	"validity_from"             timestamp(6) default CURRENT_TIMESTAMP,
	"validity_to"               timestamp(6) default CURRENT_TIMESTAMP,
	"reporting_year"            timestamp(6) default CURRENT_TIMESTAMP,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);
