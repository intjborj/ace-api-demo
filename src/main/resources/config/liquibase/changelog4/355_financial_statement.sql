


CREATE TABLE accounting.financial_report (
                                             id uuid NULL,
                                             title varchar NULL,
                                             compare_prev_month bool NULL,
                                             can_select_department bool NULL,
                                             CONSTRAINT financial_report_pk PRIMARY KEY (id)
);

ALTER TABLE accounting.financial_report
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;



CREATE TABLE accounting.line_type (
                                      id uuid NULL,
                                      report uuid NULL,
                                      caption varchar NULL,
                                      parent_line_type uuid NULL,
                                      source_type varchar NULL,
                                      order_line varchar NULL,
                                      fixed_value numeric(15,2),
                                      show_mother_account boolean NULL,
                                      show_department boolean NULL,
                                      show_sub_sub boolean NULL,
                                      CONSTRAINT line_type_pk PRIMARY KEY (id),
                                      CONSTRAINT line_type_fk FOREIGN KEY (report) REFERENCES accounting.financial_report(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                      CONSTRAINT line_type_fk_1 FOREIGN KEY (parent_line_type) REFERENCES accounting.line_type(id) ON DELETE SET NULL ON UPDATE CASCADE
);


ALTER TABLE accounting.line_type
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;


CREATE TABLE accounting.source_line_type (
                                             id uuid NULL,
                                             line_type uuid NULL,
                                             operation_type varchar NULL,
                                             CONSTRAINT source_line_type_pk PRIMARY KEY (id),
                                             CONSTRAINT source_line_type_fk FOREIGN KEY (id) REFERENCES accounting.line_type(id) ON DELETE CASCADE ON UPDATE CASCADE
);




ALTER TABLE accounting.source_line_type
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;


CREATE TABLE accounting.source_subaccount (
                                              id uuid NULL,
                                              line_type uuid NULL,
                                              subaccount uuid NULL,
                                              value_type varchar NULL,
                                              CONSTRAINT source_subaccount_pk PRIMARY KEY (id),
                                              CONSTRAINT source_subaccount_fk FOREIGN KEY (line_type) REFERENCES accounting.line_type(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                              CONSTRAINT source_subaccount_fk_1 FOREIGN KEY (subaccount) REFERENCES accounting.subaccount_setup(id) ON DELETE SET NULL ON UPDATE CASCADE
);


ALTER TABLE accounting.source_subaccount
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;



CREATE TABLE accounting.source_motheraccounts (
                                              id uuid NULL,
                                              line_type uuid NULL,
                                              chart_of_accounts uuid NULL,
                                              value_type varchar NULL,
                                              CONSTRAINT source_motheraccounts_pk PRIMARY KEY (id),
                                              CONSTRAINT source_motheraccounts_fk FOREIGN KEY (line_type) REFERENCES accounting.line_type(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                              CONSTRAINT source_motheraccounts_fk_1 FOREIGN KEY (chart_of_accounts) REFERENCES accounting.chart_of_accounts(id) ON DELETE SET NULL ON UPDATE CASCADE
);


ALTER TABLE accounting.source_motheraccounts
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;
