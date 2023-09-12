ALTER TABLE "accounting"."wtx_2307" ADD COLUMN "process" bool default false;
ALTER TABLE "accounting"."wtx_2307" ADD COLUMN "wtx_consolidated" uuid default null;
CREATE TABLE accounting.wtx_consolidated (
    id                  uuid NOT NULL primary key,
	ref_no              varchar NULL,
	supplier            uuid NULL,
	date_from           date,
	date_to             date,
	remarks             varchar NULL,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (supplier) references inventory.supplier(id)
);