create table IF NOT EXISTS hrm.allowance_templates
(
	id uuid primary key,
	name varchar,
	active BOOLEAN DEFAULT(TRUE),
	created_by varchar(50) NULL,
  	created_date timestamp NULL DEFAULT now(),
  	last_modified_by varchar(50) NULL,
  	last_modified_date timestamp NULL DEFAULT now(),
  	deleted            boolean
);
create table IF NOT EXISTS hrm.allowance_template_items
(
	template uuid null,
    CONSTRAINT template_fk FOREIGN KEY (template) REFERENCES hrm.allowance_templates(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
	allowance uuid null,
    CONSTRAINT allowance_fk FOREIGN KEY (allowance) REFERENCES hrm.allowance(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,
    note VARCHAR,
    active BOOLEAN DEFAULT(TRUE),
	created_by varchar(50) NULL,
  	created_date timestamp NULL DEFAULT now(),
  	last_modified_by varchar(50) NULL,
  	last_modified_date timestamp NULL DEFAULT now(),
  	deleted            boolean
);