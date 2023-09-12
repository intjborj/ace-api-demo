ALTER TABLE billing.investor_payment_ledger
    ADD COLUMN subscribed_share_capital                   numeric(15,2) default 0.00,
    ADD COLUMN subscription_receivable                    numeric(15,2) default 0.00,
    ADD COLUMN additional_paid_in_capital                 numeric(15,2) default 0.00,
    ADD COLUMN discount_on_share_capital                  numeric(15,2) default 0.00,
    ADD COLUMN advances_from_investors                    numeric(15,2) default 0.00,
    ADD COLUMN share_capital                              numeric(15,2) default 0.00;







