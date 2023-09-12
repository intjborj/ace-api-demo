alter table billing.price_tier_details
    drop constraint if exists fk_price_tiers_config_price_tiers,
    drop column price_tier;

drop table if exists billing.price_tiers;



