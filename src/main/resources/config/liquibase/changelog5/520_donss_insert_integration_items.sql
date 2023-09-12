INSERT INTO accounting.integration_items (id,journal_account,disabled_property,disabled_value,value_property,integration,created_by,created_date,last_modified_by,last_modified_date,deleted,source_column,multiple)
SELECT '7a0c4d29-9b44-4c4c-a12e-a85af673de1a','{"code": "600610-3700-0000", "subAccount": {"id": "5d58cdd2-e38e-4a7d-9ff3-31e8f7ccc315", "code": "3700", "domain": "com.hisd3.hismk2.domain.accounting.SubAccountSetup", "normalSide": null, "description": "PAYABLE TO INVESTOR"}, "description": "PAYABLE TO INVESTOR-PAYABLE TO INVESTOR", "motherAccount": {"id": "91ddaeea-5426-42e2-a323-7639dcee5fd2", "code": "600610", "domain": "com.hisd3.hismk2.domain.accounting.MotherAccount", "normalSide": "CREDIT", "description": "PAYABLE TO INVESTOR"}, "subSubAccount": {"id": null, "code": null, "domain": null, "normalSide": null, "description": null}, "subAccountName": "PAYABLE TO INVESTOR", "subAccountSetupId": "5d58cdd2-e38e-4a7d-9ff3-31e8f7ccc315"}',NULL,NULL,NULL,'6f875976-5595-4be2-8a90-072b5ad369d7','admin','2022-11-02 04:19:25.688','admin','2022-11-02 04:26:10.201',NULL,'change',NULL
WHERE NOT EXISTS(
    SELECT 1 FROM accounting.integration_items WHERE id = '7a0c4d29-9b44-4c4c-a12e-a85af673de1a'
  );