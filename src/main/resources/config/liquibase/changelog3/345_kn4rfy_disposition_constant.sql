INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('bc9a84ab-b248-48e8-88a7-249d94f24db2', 'DISPOSITIONS', 'DISPOSITIONS', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'DISCHARGED', 'DISCHARGED', 'DISCHARGED',
        'bc9a84ab-b248-48e8-88a7-249d94f24db2', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'DAMA/HAMA', 'DAMA/HAMA', 'DAMA/HAMA',
        'bc9a84ab-b248-48e8-88a7-249d94f24db2', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'ABSCONDED', 'ABSCONDED', 'ABSCONDED',
        'bc9a84ab-b248-48e8-88a7-249d94f24db2', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'TRANSFERRED', 'TRANSFERRED', 'TRANSFERRED',
        'bc9a84ab-b248-48e8-88a7-249d94f24db2', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'AUTOPSIED', 'AUTOPSIED', 'AUTOPSIED',
        'bc9a84ab-b248-48e8-88a7-249d94f24db2', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
