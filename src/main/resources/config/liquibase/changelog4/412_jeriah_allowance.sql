 CREATE TABLE hrm.allowance (
 	id uuid                PRIMARY KEY  DEFAULT uuid_generate_v4(),
    "template_name" varchar,
    "min_amount" numeric,
    "max_amount" numeric,
    "pay_frequency" varchar,
    "taxable" bool,
    "notes" varchar,
    "payroll_type" varchar,
    "amount" numeric,

 	created_by         varchar(50),
     created_date       timestamp(6) default CURRENT_TIMESTAMP,
     last_modified_by   varchar(50),
     last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
     deleted bool
 );
