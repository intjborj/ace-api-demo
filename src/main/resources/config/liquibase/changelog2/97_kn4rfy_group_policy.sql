create table t_group_policy
(
  id          uuid        not null
    constraint t_group_policy_pkey
      primary key,
  name        varchar(50) not null unique,
  description varchar
);

INSERT INTO t_group_policy (id, name, description)
VALUES ('24670153-dc13-4479-bf37-721508052a25', 'admin_user', 'Admin User Default Permissions');

create table t_group_policy_permission
(
  group_policy_id uuid        not null
    constraint fk_group_policy_id
      references t_group_policy,
  permission_name varchar(50) not null
    constraint fk_permission_name
      references t_permission,
  constraint t_group_policy_permission_pkey
    primary key (group_policy_id, permission_name)
);
