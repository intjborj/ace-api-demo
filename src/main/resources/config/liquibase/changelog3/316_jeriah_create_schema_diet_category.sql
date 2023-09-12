CREATE TABLE dietary.diet_categories (
	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
	"item_group_code"           varchar,
    "Item_group_description"    varchar,
	"status"                    varchar,


	created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);
