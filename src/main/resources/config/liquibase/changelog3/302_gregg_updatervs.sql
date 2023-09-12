CREATE OR REPLACE VIEW "philhealth"."rvs_codes" AS  SELECT acr."ACR_GROUPID" AS acr_groupid,
    acr."RVSCODE" AS rvscode,
    acr."EFF_DATE" AS eff_date,
        CASE
            WHEN (acr."PRIMARY_AMOUNT" IS NULL) THEN (0)::double precision
            ELSE acr."PRIMARY_AMOUNT"
        END AS primary_amount1,
    acr."PRIMARY_HOSP_SHARE" AS primary_hosp_share1,
    acr."PRIMARY_PROF_SHARE" AS primary_prof_share1,
    acr."SECONDARY_AMOUNT" AS secondary_amount,
    acr."SECONDARY_HOSP_SHARE" AS secondary_hosp_share,
    acr."SECONDARY_PROF_SHARE" AS secondary_prof_share,
    acr."PCF_AMOUNT" AS pcf_amount,
    acr."PCF_HOSP_SHARE" AS pcf_hosp_share,
    acr."PCF_PROF_SHARE" AS pcf_prof_share,
    acr."CHECK_FACILITY_H1" AS check_facility_h1,
    acr."CHECK_FACILITY_H2" AS check_facility_h2,
    acr."CHECK_FACILITY_H3" AS check_facility_h3,
    acr."CHECK_FACILITY_ASC" AS check_facility_asc,
    acr."CHECK_FACILITY_PCF" AS check_facility_pcf,
    acr."CHECK_FACILITY_MAT" AS check_facility_mat,
    acr."CHECK_FACILITY_FSDC" AS check_facility_fsdc,
    acr."CHECK_PCF_SECONDARY_CR" AS check_pcf_secondary_cr,
    acr."CHECK_ASC_SECONDARY_CR" AS check_asc_secondary_cr,
    acr."ACTIVE",
    acr."EFF_END_DATE" AS eff_end_date,
    d."DESCRIPTION" AS short_name,
    d."DESCRIPTION" AS long_name

   FROM ("philhealth"."ACR_PERRVS_RULES" acr
     LEFT JOIN "philhealth"."ACR_GROUP_RVS" d ON (((acr."RVSCODE" = d."RVSCODE") AND (acr."ACR_GROUPID" = d."ACR_GROUPID"))))
  WHERE now()::DATE between  TO_DATE(acr."EFF_DATE",'MM/DD/YYYY') and  TO_DATE(acr."EFF_END_DATE",'MM/DD/YYYY')
  ORDER BY d."RVSCODE";
COMMENT ON VIEW "philhealth"."rvs_codes" IS NULL;


DROP VIEW "philhealth"."icd_codes";
CREATE VIEW "philhealth"."icd_codes" AS  SELECT acr."ACR_GROUPID" AS acr_groupid,
    acr."ICDCODE",
    acr."EFF_DATE" AS eff_date,
        CASE
            WHEN (acr.primary_amount1 IS NULL) THEN (0)::double precision
            ELSE acr.primary_amount1
        END AS primary_amount1,
    acr.primary_hosp_share1,
    acr.primary_prof_share1,
    acr."SECONDARY_AMOUNT" AS secondary_amount,
    acr."SECONDARY_HOSP_SHARE" AS secondary_hosp_share,
    acr."SECONDARY_PROF_SHARE" AS secondary_prof_share,
    acr."PCF_AMOUNT" AS pcf_amount,
    acr."PCF_HOSP_SHARE" AS pcf_hosp_share,
    acr."PCF_PROF_SHARE" AS pcf_prof_share,
    acr."CHECK_FACILITY_H1" AS check_facility_h1,
    acr."CHECK_FACILITY_H2" AS check_facility_h2,
    acr."CHECK_FACILITY_H3" AS check_facility_h3,
    acr."CHECK_FACILITY_ASC" AS check_facility_asc,
    acr."CHECK_FACILITY_PCF" AS check_facility_pcf,
    acr."CHECK_FACILITY_MAT" AS check_facility_mat,
    acr."CHECK_FACILITY_FSDC" AS check_facility_fsdc,
    acr."CHECK_PCF_SECONDARY_CR" AS check_pcf_secondary_cr,
    acr."CHECK_ASC_SECONDARY_CR" AS check_asc_secondary_cr,
    acr."ACTIVE",
    acr."EFF_END_DATE" AS eff_end_date,
    d."DESCRIPTION" AS short_name,
    d."DESCRIPTION" AS long_name,
    acr."ICDCODE" AS diagnosis_code
   FROM ("philhealth"."ACR_PERICD_RULES" acr
     LEFT JOIN "philhealth"."ACR_GROUP_ICDS" d ON (((acr."ICDCODE" = d."ICDCODE") AND (acr."ACR_GROUPID" = d."ACR_GROUPID"))))
 WHERE now()::DATE between  TO_DATE(acr."EFF_DATE",'MM/DD/YYYY') and  TO_DATE(acr."EFF_END_DATE",'MM/DD/YYYY')
  ORDER BY d."ICDCODE";
COMMENT ON VIEW "philhealth"."icd_codes" IS NULL;