DROP TABLE  if EXISTS doh.discharge_number_deliveries;
CREATE TABLE doh.number_deliveries (
	    id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
    	total_if_delivery int,
        total_bv_delivery int,
        total_lbc_delivery int,
        total_other_delivery int,
        reporting_year timestamp    default CURRENT_TIMESTAMP,


    	created_by         varchar(50),
        created_date       timestamp(6) default CURRENT_TIMESTAMP,
        last_modified_by   varchar(50),
        last_modified_date timestamp(6) default CURRENT_TIMESTAMP

);
