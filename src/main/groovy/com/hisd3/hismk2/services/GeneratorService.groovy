package com.hisd3.hismk2.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

enum GeneratorType {
	EMPLOYEE_NO,
	PATIENT_NO,
	CASE_NO,
	OrderSlip_NO,
	OrderSlipItem_NO,
	RR_NO,
	SRR_NO,
	BILLING_NO,
	BILLING_DISCOUNT_ID,
	PR_NO,
	STOCK_REQUEST_NO,
	PO_NO,
	SUPPLIER_SUB_ACCOUNT_CODE,
	BILLING_RECORD_NO,
	DEPT_STOCK_REQ,
	GR,
	MR,
	CT,
	UTZ,
	MM,
	RF,
	DIAGNOSTICS,
	CASHIER_TERMINAL_ID,
	CASHIER_RECEIPT_ISSUANCE,
	ISSUE_NO,
	MP_NO,
	HL7_CONTROL_ID,
	QTY_ADJ,
	DIC,
	CLI,
	ALT,
	BANKID,
	COMPANYACCOUNTID,
	BEGINNING,
	PHY_COUNT,
	PKGID,
	RET_SUP,
	FISCAL,
	SHIFTING_ID,
	CDCTR,
	CASH_BASIS_NO,
	BILLING_SCHEDULE_NO,
	ACCOUNT_RECEIVABLE_NO,
	ArPAY_CODE,
	ArMEMO,
	ArPAY,
	ArTRANSFER,
	APNO,
	AR_LEDGER_NO,
	AR_MANUAL,
	AR_MANUAL_DEDUCTION,
	AR_RECORD_NO,
	DISNO,
	PCVNO,
	WTXNO,
	SUPPLIER_CODE,
	DM_NO,
	COMPANY_TYPE,
	TNO,
	SCHED_CODE,
	APP_CODE,
	LOAN_NO,
	LOAN_RECORD,

	JOURNAL_VOUCHER,
	CLAIM_BATCHNO,
	COLLECTION_DEPOSIT,

	FINANCIAL_REPORT,
	FINANCIAL_REPORT_LINE_TYPE,
	FINANCIAL_REPORT_SOURCE_TYPE,
	CASHBASIS,

	CHEQUE_ENCASHMENT_RN,
	PR_NO_CS,
	PR_NO_FA,
	PO_NO_CS,
	PO_NO_FA,
	SRR_NO_CS,
	SRR_NO_FA,

	AR_CLEARING,
	AR_CUSTOMER,
	AR_INVOICE_ITEMS,
	AR_CREDIT_NOTE,
	AR_CREDIT_NOTE_ITEMS,
	AR_TRANS_LEDGER,

}

@Service
class GeneratorService {
	
	@Autowired
	JdbcTemplate jdbcTemplate

	//======== For Soa Number =========

	@Value('${accounting.soano_start}')
	Long soaStart


	String getNextSoaNumber(String registryType, Closure closure) {
		initNextSOANumberFromRegistryType(registryType)

		def name = registryType.toLowerCase()
		def nextVal = jdbcTemplate.queryForObject(" select nextval('" + (name + "_gen") + "')", Long) as Long
		return closure(nextVal)
	}
	// No special characters and spaces please
	private void initNextSOANumberFromRegistryType(String registryType) {

		def name = registryType.toLowerCase() + "_gen"
		def count = jdbcTemplate.queryForObject(" SELECT count(*) FROM pg_class where relkind='S' and relname = ? ",
				Long,
				name
		)

		if (!count) {

			try {
				jdbcTemplate.execute(" CREATE SEQUENCE ${name} INCREMENT 1  MINVALUE 1 START ${soaStart} ")
			} catch (Exception e) {
				e.printStackTrace()
			}

		}



	}


	//======== For Soa Number  =========

	private void initGenerator(GeneratorType type) {
		
		def name = type.name().toLowerCase()
		def count = jdbcTemplate.queryForObject(" SELECT count(*) FROM pg_class where relkind='S' and relname = ? ",
				Long,
				name + "_gen"
		)
		
		if (!count) {
			
			try {
				jdbcTemplate.execute(" CREATE SEQUENCE " + (name + "_gen") + " INCREMENT 1  MINVALUE 1 START 1 ")
			} catch (Exception e) {
				e.printStackTrace()
			}
			
		}
		
	}
	
	Long getCurrentValue(GeneratorType type) {
		initGenerator(type)
		def name = type.name().toLowerCase()
		
		return jdbcTemplate.queryForObject(" SELECT last_value FROM  ${name}_gen", Long) as Long
	}
	
	String getNextValue(GeneratorType type) {
		initGenerator(type)
		
		def name = type.name().toLowerCase()
		def nextVal = jdbcTemplate.queryForObject(" select nextval('" + (name + "_gen") + "')", Long) as Long
		
		return nextVal?.toString() + ""
	}
	
	Long getNextValueLong(GeneratorType type) {
		initGenerator(type)
		
		def name = type.name().toLowerCase()
		def nextVal = jdbcTemplate.queryForObject(" select nextval('" + (name + "_gen") + "')", Long) as Long
		
		return nextVal
	}
	
	String getNextValue(GeneratorType type, Closure closure) {
		
		initGenerator(type)
		
		def name = type.name().toLowerCase()
		def nextVal = jdbcTemplate.queryForObject(" select nextval('" + (name + "_gen") + "')", Long) as Long
		return closure(nextVal)
	}

	// Generator  with Prefix
	private Boolean checkCounterExist(String prefixYear) {
		def name = prefixYear.toLowerCase() + "_gen"

		def count = jdbcTemplate.queryForObject(" SELECT count(*) FROM pg_class where relkind='S' and relname = ? ",
				Long,
				name
		)
		return count > 0
	}

	String getNextGeneratorFeatPrefix(String prefixYear, Closure closure) {
		initGeneratorFeatPrefix(prefixYear)

		def name = prefixYear.toLowerCase()
		def nextVal = jdbcTemplate.queryForObject(" select nextval('" + (name + "_gen") + "')", Long) as Long
		return closure(nextVal)
	}

	private void initGeneratorFeatPrefix(String prefixYear) {

		def name = prefixYear.toLowerCase() + "_gen"
		def count = checkCounterExist(prefixYear)

		if (!count) {
			try {
				jdbcTemplate.execute(" CREATE SEQUENCE ${name} INCREMENT 1  MINVALUE 1 START 1")
			} catch (Exception e) {
				e.printStackTrace()
			}
		}
	}

}
