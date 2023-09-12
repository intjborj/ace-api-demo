create table t_permission
(
  name        varchar(50) not null
    constraint t_permission_pkey
      primary key,
  description varchar
);

INSERT INTO t_permission (name, description)
VALUES ('add_patient', 'Permission to Add Patient');
INSERT INTO t_permission (name, description)
VALUES ('edit_patient', 'Permission to Edit Patient');
INSERT INTO t_permission (name, description)
VALUES ('delete_patient', 'Permission to Delete Patient');

create table t_user_permission
(
  user_id         bigint      not null
    constraint fk_user_id
      references t_user,
  permission_name varchar(50) not null
    constraint fk_permission_name
      references t_permission,
  constraint t_user_permission_pkey
    primary key (user_id, permission_name)
);