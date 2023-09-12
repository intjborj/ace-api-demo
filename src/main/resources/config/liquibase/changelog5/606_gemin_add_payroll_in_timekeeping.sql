ALTER TABLE payroll.timekeepings
ADD COLUMN payroll  uuid NOT NULL
                    constraint fk_timekeepings_payroll_id
                    references payroll.payrolls(id)
                    on update cascade on delete restrict;
