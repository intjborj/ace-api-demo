CREATE TABLE IF NOT EXISTS "hrm"."department_schedule" (
    "id"                            uuid NOT NULL,
    "department"                      uuid NOT NULL,
    "date_time_start"               timestamp,
    "date_time_end"                 timestamp,
    "meal_break_start"              timestamp,
    "meal_break_end"                timestamp,
    "color"                         varchar(255),
    "deleted"                       bool,
    "created_by"                    varchar(50),
    "created_date"                  timestamp DEFAULT now(),
    "last_modified_by"              varchar(50),
    "last_modified_date"            timestamp DEFAULT now()
);