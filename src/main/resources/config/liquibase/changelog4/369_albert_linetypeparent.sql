ALTER TABLE accounting.source_line_type ADD linetype_parent uuid NULL;
ALTER TABLE accounting.source_line_type ADD CONSTRAINT source_line_type_fk_1 FOREIGN KEY (linetype_parent) REFERENCES accounting.line_type(id) ON DELETE RESTRICT ON UPDATE CASCADE;
