
create table "fixed_assets"."fixed_asset_items"(
    id   uuid not null primary key,
    fixed_asset uuid null,
                CONSTRAINT fixed_asset_fk FOREIGN KEY (fixed_asset) REFERENCES fixed_assets.fixed_assets(id)
                 ON DELETE SET NULL
                 ON UPDATE CASCADE,
    items uuid null,
                CONSTRAINT item_fk FOREIGN KEY (items) REFERENCES inventory.item(id)
                ON DELETE SET NULL
                ON UPDATE CASCADE,
    serial_no varchar(50),
    status varchar(50),
    depreciable boolean,
    latest_dept uuid null,
                CONSTRAINT latest_dept_fk FOREIGN KEY(latest_dept) REFERENCES public.departments(id)
                ON DELETE SET NULL
                ON UPDATE CASCADE,
    latest_cost numeric,
    latest_est_useful_life varchar(50),
    latest_est_salvage_value numeric,
    notes varchar(50),

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        boolean
);

