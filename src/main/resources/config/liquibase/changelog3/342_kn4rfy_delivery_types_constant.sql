INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('0d8cfb28-92be-4409-9285-2d6f92405961', 'DELIVERY TYPES', 'DELIVERY TYPES', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'NORMAL', 'NORMAL', 'NORMAL',
        '0d8cfb28-92be-4409-9285-2d6f92405961', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'CAESARIAN', 'CAESARIAN', 'CAESARIAN',
        '0d8cfb28-92be-4409-9285-2d6f92405961', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
