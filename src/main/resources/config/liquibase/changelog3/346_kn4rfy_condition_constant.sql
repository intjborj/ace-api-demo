INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('f11b2597-4dc3-452f-9b33-89cee2ad0e14', 'CONDITIONS', 'CONDITIONS', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'IMPROVED', 'IMPROVED', 'IMPROVED',
        'f11b2597-4dc3-452f-9b33-89cee2ad0e14', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'UNIMPROVED', 'UNIMPROVED', 'UNIMPROVED',
        'f11b2597-4dc3-452f-9b33-89cee2ad0e14', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'RECOVERED', 'RECOVERED', 'RECOVERED',
        'f11b2597-4dc3-452f-9b33-89cee2ad0e14', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'EXPIRED', 'EXPIRED', 'EXPIRED',
        'f11b2597-4dc3-452f-9b33-89cee2ad0e14', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
