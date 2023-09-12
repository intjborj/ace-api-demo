create table referential.doh_icd_category
(
	id  uuid not null primary key,
	icd_10_code varchar(10),
	diseases_conditions varchar(200),
	created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
