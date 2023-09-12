Alter Table referential.doh_positions
add column is_others 			boolean,
add column created_by          	varchar(50) NULL,
add column created_date        	timestamp NULL DEFAULT now(),
add column last_modified_by    	varchar(50) NULL,
add column last_modified_date  	timestamp NULL DEFAULT now(),
add column deleted 				boolean;

Alter Table hrm.employees
add column position uuid;

INSERT INTO referential.doh_positions (id, poscode, postdesc, postype, poscode_parent, created_by, created_date, last_modified_by, last_modified_date, is_others, deleted)
SELECT id, poscode, postdesc, null,null, 'admin', created_date, 'admin', last_modified_date, true, deleted
FROM referential.doh_position_others;

update hrm.employees e
set position = (
select p.id from referential.doh_positions p
where p.is_others is not true
and p.poscode = e.position_code
)
where e.position is null;

update hrm.employees e
set position = (
select p.id from referential.doh_positions p
where p.is_others = true
and p.poscode = e.position_code_others
and p.postdesc = e.position_type
)
where e.position is null;

DROP TABLE  referential.doh_position_others;


