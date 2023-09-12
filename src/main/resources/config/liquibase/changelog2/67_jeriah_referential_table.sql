create schema referential;

create table referential.icd_categories
(
  id                             uuid not null primary key,
  icd_category_code              varchar,
  icd_category_desc              varchar,

  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        boolean
);

create table referential.icd_codes
(
    id                          uuid not null primary key,
    icd_code                    varchar,
    icd_desc                    varchar,
    icd_category                uuid Null,
                                CONSTRAINT icd_categories_fk FOREIGN KEY (id)  REFERENCES referential.icd_categories(id)
);