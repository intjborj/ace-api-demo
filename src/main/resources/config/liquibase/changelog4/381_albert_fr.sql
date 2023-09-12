ALTER TABLE "accounting"."source_subaccount_exclude_mother" ADD FOREIGN KEY ("source_subaccount") REFERENCES "accounting"."source_subaccount" ("id") ON UPDATE CASCADE ON DELETE CASCADE;

ALTER TABLE "accounting"."source_subaccount_exclude_mother" ADD FOREIGN KEY ("mother_account") REFERENCES "accounting"."chart_of_accounts" ("id") ON UPDATE CASCADE ON DELETE SET NULL;