ALTER TABLE accounting.source_line_type DROP CONSTRAINT source_line_type_fk;
ALTER TABLE accounting.source_line_type ADD CONSTRAINT source_line_type_fk FOREIGN KEY (line_type) REFERENCES accounting.line_type(id) ON DELETE CASCADE ON UPDATE CASCADE;
