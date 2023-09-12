----index ---
CREATE INDEX IF NOT EXISTS item_desc_long_idx ON inventory.item (desc_long);
CREATE INDEX IF NOT EXISTS item_sku_idx ON inventory.item (sku);
CREATE INDEX IF NOT EXISTS item_item_code_idx ON inventory.item (item_code);