CREATE TABLE ancillary.orderslip_item_package_content  (
    id                  uuid NOT NULL primary key,
    order_slip_item     uuid not null,
    item                uuid not null,
    item_name           varchar not null,
    qty                 int default 0,
    department          uuid not null,
    ref_billing_item    uuid default null,

    created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key(order_slip_item) references ancillary.orderslip_item(id),
	foreign key(item) references inventory.item(id),
	foreign key(department) references public.departments(id),
	foreign key(ref_billing_item) references billing.billing_item(id)
)