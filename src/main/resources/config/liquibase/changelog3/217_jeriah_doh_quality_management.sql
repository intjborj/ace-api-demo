
CREATE TABLE doh.quality_management (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	"quality_mgmttype"          varchar,
    "description"               varchar,
	"certifying_body"           varchar,
	"philHealth_accredition"    varchar,
	"validity_from"             varchar,
	"validity_to"               varchar,
	"reporting_year"            int,

	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);