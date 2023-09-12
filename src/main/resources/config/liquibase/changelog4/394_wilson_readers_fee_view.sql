-- accounting.readers_fees source

CREATE OR REPLACE VIEW accounting.readers_fees
AS SELECT bi.id,
    bi.transaction_date,
    bi.billing,
    bi.record_no,
    bi.description,
    bi.department,
    bi.debit AS price,
    bi.pricing_tier,
    bi.package_id,
    coalesce(bi.rf_fee,0) as rf_fee,
    (bi.rf_details::json ->> 'serviceId'::text)::uuid AS serviceid,
    (bi.rf_details::json ->> 'doctorsId'::text)::uuid AS doctorsid,
    s.id AS supplier,
    (bi.rf_details::json ->> 'rfTableId'::text)::uuid AS rftableid,
     coalesce((bi.rf_details::json ->> 'percentage'::text)::numeric,0) AS percentage,
    bi.registry_type_charged,
    bi.ap_process
   FROM billing.billing_item bi
     LEFT JOIN inventory.supplier s ON s.employee_id = ((bi.rf_details::json ->> 'doctorsId'::text)::uuid)
  WHERE bi.item_type::text = 'DIAGNOSTICS'::text AND bi.status::text = 'ACTIVE'::text
  ORDER BY bi.transaction_date DESC;