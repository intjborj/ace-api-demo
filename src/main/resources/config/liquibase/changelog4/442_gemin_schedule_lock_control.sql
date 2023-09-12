CREATE TABLE "hrm"."schedule_locks" (
    id                                                uuid NOT NULL PRIMARY KEY,
    date                                              timestamp,
    is_locked                                         bool,

    created_by                                        varchar(50),
    created_date                                      timestamp DEFAULT now(),
    last_modified_by                                  varchar(50),
    last_modified_date                                timestamp DEFAULT now(),
    deleted                                           bool
);