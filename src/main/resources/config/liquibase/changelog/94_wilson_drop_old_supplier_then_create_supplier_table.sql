drop table if exists "inventory"."supplier";
create table inventory.supplier
(
  id                            uuid not null primary key,
  supplier_code                 varchar,
  supplier_fullname             varchar,
  supplier_tin                  varchar,
  supplier_email                varchar,
  payment_terms                 uuid not null,
  supplier_entity               varchar,
  supplier_types                uuid not null,
  credit_limit                  numeric,
  is_vatable                    bool,
  is_vat_inclusive              bool,
  remarks                       varchar,
  lead_time                     int4,
  primary_address               varchar,
  primary_telphone              varchar,
  primary_contactperson         varchar,
  primary_fax                   varchar,
  secondary_address             varchar,
  secondary_telphone            varchar,
  secondary_contactperson       varchar,
  secondary_fax                 varchar,
  is_active                     bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (payment_terms) REFERENCES  inventory.payment_terms (id),
  FOREIGN KEY (supplier_types) REFERENCES  inventory.supplier_types (id)
);