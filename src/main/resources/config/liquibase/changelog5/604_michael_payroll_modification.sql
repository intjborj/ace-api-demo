create table payroll.payroll_allowances(
    id uuid not null primary key,
    payroll uuid not null,
    description varchar,
    status varchar (20) not null,
    finalized_by uuid,
    finalized_date timestamp,
    CONSTRAINT payroll_fk FOREIGN KEY(payroll) REFERENCES payroll.payrolls(id),
    CONSTRAINT finalized_by_fk FOREIGN KEY(finalized_by) REFERENCES hrm.employees(id)
);

create table payroll.payroll_employee_allowances(
    id uuid not null primary key,
	employee uuid not null,
	status varchar (20) not null,
	payroll_allowance uuid not null,
	approved_by uuid,
	approved_date timestamp,
	CONSTRAINT employee_fk FOREIGN KEY(employee) REFERENCES hrm.employees(id),
	CONSTRAINT approved_by_fk FOREIGN KEY(approved_by) REFERENCES hrm.employees(id),
 	CONSTRAINT payroll_allowance_fk FOREIGN KEY(payroll_allowance) REFERENCES payroll.payroll_allowances(id)
);



create table payroll.payroll_employee_allowance_items(
    id uuid not null primary key,
	payroll_employee_allowance uuid not null,
	name varchar not null,
	amount numeric (15,2) not null,
	taxable boolean not null,
	CONSTRAINT payroll_employee_allowance_fk FOREIGN KEY(payroll_employee_allowance) REFERENCES payroll.payroll_employee_allowances(id)
	
);

create table payroll.payroll_contributions(
    id uuid not null primary key,
    payroll uuid not null,
    status varchar (20) not null,
    finalized_by uuid,
    finalized_date timestamp,
    CONSTRAINT payroll_fk FOREIGN KEY(payroll) REFERENCES payroll.payrolls(id),
    CONSTRAINT finalized_by_fk FOREIGN KEY(finalized_by) REFERENCES hrm.employees(id)
);


create table payroll.payroll_employee_contributions(
    id uuid not null primary key,
    employee uuid not null,
    status varchar (20) not null,
    payroll_contribution uuid not null,
    sss_ee  numeric(15,2) default 0,
    sss_er numeric(15,2) default 0,
    phic_ee numeric(15,2) default 0,
    phic_er numeric(15,2) default 0,
    hdmf_er numeric(15,2) default 0,
    hdmf_ee numeric(15,2) default 0,
    approved_by uuid,
	approved_date timestamp,
    CONSTRAINT payroll_contribution_fk FOREIGN KEY(payroll_contribution) REFERENCES payroll.payroll_contributions(id),
    CONSTRAINT approved_by_fk FOREIGN KEY(approved_by) REFERENCES hrm.employees(id)
);
	