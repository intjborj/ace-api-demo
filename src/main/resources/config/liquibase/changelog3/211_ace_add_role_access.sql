insert into t_authority(name) values ('PRICE_TIERS');
insert into t_authority(name) values ('PRICE_TIER_MODIFIERS');
insert into t_authority(name) values ('PRICE_TIER_MEDICINES');
insert into t_authority(name) values ('PRICE_TIERS_SUPPLIES');

-- service permissions
INSERT INTO t_permission (name, description)
SELECT 'allow_update_service_tier_prices', 'Permission to update service tier prices'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_update_service_tier_prices'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_delete_service_tier_prices', 'Permission to delete service tier prices'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_delete_service_tier_prices'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_execute_service_tier_prices', 'Permission to execute service tier prices'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_execute_service_tier_prices'
  );

-- items permissions
INSERT INTO t_permission (name, description)
SELECT 'allow_update_item_tier_prices', 'Permission to update items tier prices'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_update_item_tier_prices'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_delete_item_tier_prices', 'Permission to delete items tier prices'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_delete_item_tier_prices'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_execute_item_tier_prices', 'Permission to execute items tier prices'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_execute_item_tier_prices'
  );

-- modifier permissions
INSERT INTO t_permission (name, description)
SELECT 'allow_execute_price_tier_modifier', 'Permission to execute price tier modifier'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_execute_price_tier_modifier'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_update_price_tier_modifier', 'Permission to update price tier modifier'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_update_price_tier_modifier'
  );

INSERT INTO t_permission (name, description)
SELECT 'allow_delete_price_tier_modifier', 'Permission to delete price tier modifier'
WHERE NOT EXISTS(
    SELECT 1 FROM public.t_permission WHERE name = 'allow_delete_price_tier_modifier'
  );