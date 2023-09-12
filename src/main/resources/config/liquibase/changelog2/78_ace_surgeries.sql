create table if not exists "referential"."doh_surgical_codes"
(
    id uuid not null primary key,
    proccode varchar,
    procdesc varchar,
    optycode varchar,
    protcode varchar,
    procuval varchar,
    procrem varchar,
    procstat varchar,
    proclock varchar,
    datemod varchar,
    updsw varchar,
    altpcode varchar,
    altpdesc varchar,
    priden varchar,
    prmapto varchar,
    prsect varchar,
    prvfa varchar,
    prdetsec varchar,
    prregn varchar,
    prextyp varchar,
    prspeco varchar,
    costcenter varchar,
    procreslt varchar,
    rvu varchar,
    restemplate varchar,
    priority varchar,



    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        boolean
);

alter table "philhealth"."ricd10" rename to doh_icd_codes;

drop table if exists "referential"."doh_icd_codes";
drop table if exists "referential"."doh_icd_categories";

alter table "philhealth"."doh_icd_codes" SET SCHEMA referential;


