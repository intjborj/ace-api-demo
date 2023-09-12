alter table payroll.accumulated_logs
  ADD COLUMN final_logs jsonb NULL,
  ADD COLUMN original_logs jsonb NULL

    ;
