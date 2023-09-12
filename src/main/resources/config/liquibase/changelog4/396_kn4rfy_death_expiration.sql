INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('bc317496-ac84-4a7b-b67d-0126c7062245', 'DEATH/EXPIRATION', 'DEATH/EXPIRATION', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'HOSPITAL DEATH', 'HOSPITAL DEATH', 'HOSPITAL DEATH',
        'bc317496-ac84-4a7b-b67d-0126c7062245', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'DEAD ON ARRIVAL', 'DEAD ON ARRIVAL', 'DEAD ON ARRIVAL',
        'bc317496-ac84-4a7b-b67d-0126c7062245', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'ER DEATH', 'ER DEATH', 'ER DEATH',
        'bc317496-ac84-4a7b-b67d-0126c7062245', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
