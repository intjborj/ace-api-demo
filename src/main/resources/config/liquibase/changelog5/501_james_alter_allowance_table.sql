ALTER TABLE hrm.allowance
DROP COLUMN min_amount,
DROP COLUMN max_amount;

ALTER TABLE hrm.allowance
RENAME template_name TO name;