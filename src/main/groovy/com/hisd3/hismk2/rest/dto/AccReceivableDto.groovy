package com.hisd3.hismk2.rest.dto

import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import org.xmlsoap.schemas.soap.encoding.Decimal

import java.time.Instant


class AccReceivableDto {

}

class BillingScheduleFieldsDto {
	String date
	String hmoAddress
	String upperLabel
	String refNo
	String soaNo
}

class BillingScheduleDto {
	String transDate
	String soa
	String patientName
	String approvalCode
	BigDecimal hci
	BigDecimal pf
	BigDecimal amount
}

class BillingScheduleSignDto {
	String signatureHeader
	String signaturies
	String position
}

class AccReceivableHeaderDto {
	String invoiceSoaReference
	String entityName
	String particulars
}


class ManualBillingDto {
	String description
	BigDecimal charges
	BigDecimal credits
	BigDecimal balance
}


class ARForJournalViewDto {
	UUID accountReceivableItems
	String type
	BigDecimal amount
}


class ArArrayDto {
	Map<String, Object> from
	Map<String, Object> to
	BigDecimal amount
	String reference
}


class ArTransferForJournalViewDto {
	UUID company
	BigDecimal amount
}


class OtherARForJournalViewDto {
	UUID personalAccount
}

class AccRecDto {
	UUID accRecId
	Map<String, Object> fields
	UUID billingScheduleId
}

class AccRecOtherDto {
	UUID accRecId
	String type
	UUID personalAccount
	Map<String, Object> fields
	ArrayList<Map<String, Object>> fieldsItems
}

class AccRecPaymentTransferDto {
	UUID arTransId
	String type
	Map<String, Object> fields
	ArrayList<Map<String, Object>> fieldsItems
	Map<String,Object>  header
	ArrayList<Map<String, Object>> entries
}

class EditedJournalEntryDto {
	String code
	String description
	BigDecimal debit
	BigDecimal credit
}
