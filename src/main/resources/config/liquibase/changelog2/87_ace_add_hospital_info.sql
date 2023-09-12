alter table "hospital_configuration"."hospital_info"
    drop column if exists "specilaty_info",
    add column "specialty_specify" int;