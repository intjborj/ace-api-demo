
create table regions
(
	id uuid NOT NULL,
	name varchar(100),
	country_id int
);

create table province_lists
(
  id uuid NOT NULL,
	name varchar(100),
	region_id uuid
);

create table municipalities
(
	id uuid NOT NULL,
	name varchar(100),
	province_id uuid
);

create table barangays
(
	id uuid NOT NULL,
	name varchar(100),
	municipality_id uuid
);

