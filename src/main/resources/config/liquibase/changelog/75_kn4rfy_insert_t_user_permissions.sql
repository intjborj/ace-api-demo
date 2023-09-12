INSERT INTO t_permission (name, description)
VALUES ('add_employee', 'Permission to Add Employee');
INSERT INTO t_permission (name, description)
VALUES ('edit_employee', 'Permission to Edit Employee');
INSERT INTO t_permission (name, description)
VALUES ('delete_employee', 'Permission to Delete Employee');

INSERT INTO t_user_permission (user_id, permission_name)
VALUES (2, 'add_employee');
INSERT INTO t_user_permission (user_id, permission_name)
VALUES (2, 'edit_employee');
INSERT INTO t_user_permission (user_id, permission_name)
VALUES (2, 'delete_employee');