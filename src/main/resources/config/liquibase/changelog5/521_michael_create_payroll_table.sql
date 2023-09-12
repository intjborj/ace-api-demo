
create table "payroll"."payrolls"(
   id   uuid not null primary key,
    title varchar (50),
    description varchar,
    status varchar (20),
    start_date timestamp(6) default CURRENT_TIMESTAMP,
    end_date timestamp(6) default CURRENT_TIMESTAMP,
    finalized_by varchar(50),
    finalized_date timestamp(6) default CURRENT_TIMESTAMP,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        boolean
);

ALTER TABLE payroll.accumulated_logs
    DROP COLUMN totals
