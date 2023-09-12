
create table "fixed_assets"."fixed_asset_depreciation"(
    id   uuid not null primary key,
    fixed_asset_item  uuid null,
                CONSTRAINT fixed_asset_item_fk FOREIGN KEY (fixed_asset_item) REFERENCES fixed_assets.fixed_asset_items(id)
                 ON DELETE SET NULL
                 ON UPDATE CASCADE,
    const numeric,
    est_useful_life varchar,
    est_salvage_value numeric,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        boolean
);

