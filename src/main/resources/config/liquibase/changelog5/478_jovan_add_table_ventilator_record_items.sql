CREATE TABLE IF NOT EXISTS pms.ventilator_record_items(
    id UUID,
    ventilator_record UUID,
    fi02 int,
    peep int,
    sputum_character varchar,
    sputum_character_remarks varchar,
    sputum_result varchar,
    sputum_result_remarks varchar,


    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);