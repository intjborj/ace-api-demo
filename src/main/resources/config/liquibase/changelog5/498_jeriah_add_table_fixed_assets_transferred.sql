
create table "fixed_assets"."fixed_asset_transfers"(
    id   uuid not null primary key,
    fixed_asset_item  uuid null,
                CONSTRAINT fixed_asset_item_fk FOREIGN KEY (fixed_asset_item) REFERENCES fixed_assets.fixed_asset_items(id)
                 ON DELETE SET NULL
                 ON UPDATE CASCADE,
    source_dept uuid null,
                CONSTRAINT source_dept FOREIGN KEY (source_dept) REFERENCES public.departments(id)
                ON DELETE SET NULL
                ON UPDATE CASCADE,
    destination_dept uuid null,
                CONSTRAINT destination_dept_fk FOREIGN KEY(destination_dept) REFERENCES public.departments(id)
                ON DELETE SET NULL
                ON UPDATE CASCADE,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        boolean
);

