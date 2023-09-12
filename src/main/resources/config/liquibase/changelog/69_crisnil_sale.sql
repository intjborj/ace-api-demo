create schema sales;


create table sales.sales_transaction
(
  id UUID,
  customer_name varchar,
  customer_id UUID,
  address varchar,
  date_purchase timestamp,
  cashier_id UUID,
  cashier_name varchar,
  status varchar,
  created_by varchar,
  created_date timestamp,
  last_modified_by varchar,
  last_modified_date timestamp,
  deleted boolean,
  void boolean
);


create table sales.sales_transaction_item
(
  id UUID,
  sales_transaction UUID,
  item UUID,
  qty numeric,
  base_price numeric,
  non_vat boolean,
  amount numeric,
  status varchar,
  created_by varchar,
  created_date timestamp,
  last_modified_by varchar,
  last_modified_date timestamp,
  deleted boolean,
  void boolean
);
