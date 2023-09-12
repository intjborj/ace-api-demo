drop table if exists hosp_operation_hai;
drop table if exists hosp_operations_minor_opt;
drop table if exists hosp_operations_mortality_deaths;
drop table if exists hosp_opt_descharges_morbidity;
drop table if exists hosp_opt_descharges_specialty;
drop table if exists hosp_opt_descharges_specialty_others;

drop table if exists hosp_opt_discharges_er;
drop table if exists hosp_opt_discharges_ev;
drop table if exists hosp_opt_discharges_number_deliveries;
drop table if exists hosp_opt_discharges_opd;
drop table if exists hosp_opt_discharges_opv;
drop table if exists hosp_opt_discharges_testing;
drop table if exists hosp_opt_summary_of_patient;

drop table if exists hospital_configuration.gen_info_classification;
drop table if exists hospital_configuration.gen_info_bed_capacity;
drop table if exists hospital_configuration.gen_info_quality_management;

alter table hospital_configuration.hospital_info
    add column service_capability int4,
    add column ic_general int4,
    add column specialty int,
    add column specilaty_specify varchar,
    add column trauma_capability int,
    add column nature_of_ownership int,
    add column government int,
    add column "national" int4,
    add column "local" int4,
    add column "private" int;

alter table hospital_configuration.hospital_info
  add column "abc" int4,
  add column "implementing_beds" int4,
  add column "bor" int4;

alter table hospital_configuration.hospital_info
  add column "quality_mgmt_type" int4,
  add column "qm_description" varchar(255),
  add column "certifying_body" varchar(250),
  add column "qm_philhealth_accreditation" int4,
  add column "validity_from" date,
  add column "validity_to" date;





