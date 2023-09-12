
ALTER TABLE billing.investor_subscriptions
    ALTER COLUMN shares                                TYPE numeric(15,4),
    ALTER COLUMN subscription_price                    TYPE numeric(15,4),
    ALTER COLUMN par_value                             TYPE numeric(15,4);
