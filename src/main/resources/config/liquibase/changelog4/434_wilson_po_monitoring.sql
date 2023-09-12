CREATE TABLE inventory.po_delivery_monitoring (
    id                  uuid NOT NULL primary key,
	purchase_order_item uuid NULL,
	receiving           uuid NULL,
	receiving_item      uuid NULL,
	delivered_qty       int,

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

    foreign key (purchase_order_item) references inventory.purchase_order_items(id),
	foreign key (receiving) references inventory.receiving_report(id),
	foreign key (receiving_item) references inventory.receiving_report_items(id)
);