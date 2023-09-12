package com.hisd3.hismk2.rest.dto

import groovy.transform.Canonical

@Canonical
class ARInvoiceDto {
	String invoiceDate
	String soa_no
	String itemName
	String approvalCode
	BigDecimal totalHCIAmount
	BigDecimal totalPFAmount
	BigDecimal totalAmountDue
}

@Canonical
class ARInvoiceBankDto {
	String bank_name
	String bank_branch
	String bank_account
}



class ARInvoiceFieldsDto {
	String customer_account_number
	String customer_name
	String invoice_number
	String invoice_date
	String customer_address
	String invoice_intro
	String due_date
	String prepared_by
	String noted_by
}

@Canonical
class ArCreditNoteItemsDTO {
	String reference
	String description
	String itemName
	String invoiceNo
	BigDecimal totalAmountDue
}


@Canonical
class ArCreditNoteJournalEntryDTO {
	String transactionDate
	String referenceNo
	String accountCode
	String accountName
	BigDecimal debit
	BigDecimal credit
}


class ARCreditNoteFieldsDto {
	String customer_account_number
	String customer_name
	String customer_address
	String cn_number
	String cn_date
	String prepared_by
	String noted_by
	String audited_by
	String approved_by
}