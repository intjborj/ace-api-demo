INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('eb89cdb9-4d8f-4538-bef4-93601bfc7fe7', 'ROOM STATUSES', 'ROOM STATUSES', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'AVAILABLE', 'AVAILABLE', 'AVAILABLE',
        'eb89cdb9-4d8f-4538-bef4-93601bfc7fe7', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'OCCUPIED', 'OCCUPIED', 'OCCUPIED',
        'eb89cdb9-4d8f-4538-bef4-93601bfc7fe7', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'MAINTENANCE', 'MAINTENANCE', 'MAINTENANCE',
        'eb89cdb9-4d8f-4538-bef4-93601bfc7fe7', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'CLEANING', 'CLEANING', 'CLEANING',
        'eb89cdb9-4d8f-4538-bef4-93601bfc7fe7', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
