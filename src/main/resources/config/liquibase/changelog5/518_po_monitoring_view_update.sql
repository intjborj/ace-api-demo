-- inventory.po_items_with_monitoring source
DROP VIEW IF EXISTS inventory.po_items_with_monitoring;
CREATE OR REPLACE VIEW inventory.po_items_with_monitoring
AS SELECT poi.id,
    poi.purchase_order,
    po.po_number,
    poi.item,
    i.desc_long,
    po.supplier,
    s.supplier_fullname,
    poi.quantity,
    poi.supplier_last_price,
    poi.pr_nos,
    poi.qty_in_small,
    poi.type,
    poi.type_text,
    poi.created_by,
    poi.created_date,
    poi.last_modified_by,
    poi.last_modified_date,
    poi.deleted,
    poi.receiving_report,
    COALESCE(dr.delivered_qty, 0::bigint) AS delivered_qty,
    COALESCE(poi.qty_in_small - dr.delivered_qty, poi.qty_in_small::bigint) AS delivery_balance,
    poi.delivery_status
   FROM inventory.purchase_order_items poi
     LEFT JOIN inventory.pomonreference dr ON dr.purchase_order_item = poi.id
     LEFT JOIN inventory.item i ON i.id = poi.item
     LEFT JOIN inventory.purchase_order po ON po.id = poi.purchase_order
     LEFT JOIN inventory.supplier s  ON s.id = po.supplier
  WHERE poi.deleted IS NULL OR poi.deleted = false
  ORDER BY poi.purchase_order;