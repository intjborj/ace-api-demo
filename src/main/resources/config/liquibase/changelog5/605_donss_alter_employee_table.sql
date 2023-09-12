ALTER TABLE hrm.employees
ADD COLUMN IF NOT EXISTS birthplace VARCHAR default null,
ADD COLUMN IF NOT EXISTS educational_background jsonb