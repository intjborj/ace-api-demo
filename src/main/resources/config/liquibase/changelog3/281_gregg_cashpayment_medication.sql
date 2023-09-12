ALTER TABLE "inventory"."stock_request_item"
	ADD COLUMN "for_cash_payment" bool,
	ADD COLUMN "billing_item_no" varchar;