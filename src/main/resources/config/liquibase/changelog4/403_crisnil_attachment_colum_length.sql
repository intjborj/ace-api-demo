alter table pms.attachment alter column file_name type varchar(255) using file_name::varchar(255);

alter table pms.attachment alter column mimetype type varchar(255) using mimetype::varchar(255);

