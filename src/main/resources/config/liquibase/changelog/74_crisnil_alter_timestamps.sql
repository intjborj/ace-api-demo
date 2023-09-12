alter table ancillary.orderslips alter column created_date type timestamp without time zone using created_date::timestamp without time zone;

alter table ancillary.orderslips alter column created_date set default current_timestamp;

alter table ancillary.orderslips alter column last_modified_date type timestamp without time zone using last_modified_date::timestamp without time zone;

alter table ancillary.orderslips alter column last_modified_date set default current_timestamp;

alter table ancillary.orderslips alter column time_started type timestamp without time zone using time_started::timestamp without time zone;

alter table ancillary.orderslips alter column time_completed type timestamp without time zone using time_completed::timestamp without time zone;

alter table ancillary.orderslip_item alter column time_started type timestamp without time zone using time_started::timestamp without time zone;

alter table ancillary.orderslip_item alter column time_completed type timestamp without time zone using time_completed::timestamp without time zone;

alter table ancillary.orderslip_item alter column created_date type timestamp without time zone using created_date::timestamp without time zone;

alter table ancillary.orderslip_item alter column last_modified_date type timestamp without time zone using last_modified_date::timestamp without time zone;

alter table ancillary.orderslip_item
  add accession varchar;

alter table ancillary.orderslip_item
  add submittedto3rdparty boolean default false;

alter table ancillary.diagnostic_results drop constraint orderslip;

alter table ancillary.diagnostic_results rename column orderslip to orderslip_item;

alter table sales.sales_transaction alter column date_purchase type timestamp without time zone using date_purchase::timestamp without time zone;

alter table sales.sales_transaction alter column created_date type timestamp without time zone using created_date::timestamp without time zone;

alter table sales.sales_transaction alter column last_modified_date type timestamp without time zone using last_modified_date::timestamp without time zone;

alter table sales.sales_transaction_item alter column created_date type timestamp without time zone using created_date::timestamp without time zone;

alter table sales.sales_transaction_item alter column last_modified_date type timestamp without time zone using last_modified_date::timestamp without time zone;
