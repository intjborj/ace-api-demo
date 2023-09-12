


--HR New Permissions
--
--1. Permission to Create Leave Request — allow_create_leave_request
INSERT INTO public.t_permission(name, description)
SELECT 'allow_create_leave_request', 'Permission to Create Leave Request'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_create_leave_request'
  );

--2. Permission to Reject Leave Request — allow_reject_leave_request
INSERT INTO public.t_permission(name, description)
SELECT 'allow_reject_leave_request', 'Permission to Reject Leave Request'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_reject_leave_request'
  );

--3. Permission to Approve Leave Request — allow_approve_leave_request
INSERT INTO public.t_permission(name, description)
SELECT 'allow_approve_leave_request', 'Permission to Approve Leave Request'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_approve_leave_request'
  );

--4. Permission to HR Reject Leave Request — allow_hr_reject_leave_request
INSERT INTO public.t_permission(name, description)
SELECT 'allow_hr_reject_leave_request', 'Permission to HR Reject Leave Request'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_hr_reject_leave_request'
  );

--5. Permission to HR Approve Leave Request — allow_hr_approve_leave_request
INSERT INTO public.t_permission(name, description)
SELECT 'allow_hr_approve_leave_request', 'Permission to HR Approve Leave Request'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_hr_approve_leave_request'
  );


--6. Permission to Approve Leave Request Approval — allow_approve_leave_request_approval
INSERT INTO public.t_permission(name, description)
SELECT 'allow_approve_leave_request_approval', 'Permission to Approve Leave Request Approval'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_approve_leave_request_approval'
  );


--7. Permission to HR Revert Leave Request — allow_hr_revert_leave_request
INSERT INTO public.t_permission(name, description)
SELECT 'allow_hr_revert_leave_request', 'Permission to HR Revert Leave Request'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_hr_revert_leave_request'
  );


--8. Permission to Reject Leave Request Approval — allow_reject_leave_request_approval
INSERT INTO public.t_permission(name, description)
SELECT 'allow_reject_leave_request_approval', 'Permission to Reject Leave Request Approval'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_reject_leave_request_approval'
  );



--HR New Permissions
--
--1. LEAVE_REQUEST_APPROVER
insert into t_authority(name) values ('LEAVE_REQUEST_APPROVER');
--2. LEAVE_REQUEST_MANAGER
insert into t_authority(name) values ('LEAVE_REQUEST_MANAGER');
