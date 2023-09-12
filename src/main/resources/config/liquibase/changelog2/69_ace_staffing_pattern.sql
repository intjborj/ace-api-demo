create table referential.doh_positions
(
    id uuid not null primary key,
    poscode	int,
    postdesc varchar,
    postype	int,
    poscode_parent int
);