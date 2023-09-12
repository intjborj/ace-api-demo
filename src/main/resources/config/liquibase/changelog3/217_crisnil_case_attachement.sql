create table pms.attachment
(
	id uuid
		constraint attachment_pk
			primary key,
	patient uuid not null,
	"case" uuid,
	file_name varchar(100),
	description varchar(200),
	mimetype varchar(50),
	url_path varchar(255),
	deleted boolean,
	created_by varchar(100),
	created_date timestamp,
	last_modified_by varchar(100),
	last_modified_date timestamp
);

