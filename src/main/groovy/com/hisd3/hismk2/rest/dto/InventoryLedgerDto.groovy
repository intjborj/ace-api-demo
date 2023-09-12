package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.Department
import groovy.transform.TupleConstructor

class InventoryLedgerDto {
	String id
	String ledgerNo
	String poId
	String poItem
	Department sourceDep
	Department destDep
	String ledgerDate
	String typeId
	String typeDesc
	String itemId
	String itemDesc
	String unit
	Integer qty
	BigDecimal unitCost
	BigDecimal discountCost
	Boolean isFg
	Boolean isDiscount
	Boolean isPartial
	Boolean isCompleted
	UUID account
	
}

class PostLedgerDto {
	String id
	String ledgerNo
	String poId
	String poItem
	Department source
	Department destination
	String date
	String typeId
	String itemId
	Integer qty
	BigDecimal unitcost
	Boolean isFg
	Boolean isDiscount
	Boolean isPartial
	Boolean isCompleted
	UUID account

}

@TupleConstructor
class POMonitoringDto {
	UUID purchaseOrderItem
	UUID receivingReport
	UUID receivingReportItem
	Integer quantity
	String status
}

class StockCard {
	String id
	String source_dep
	String source_department
	String destination_dep
	String dest_department
	String document_types
	String document_code
	String document_desc
	String item
	String sku
	String item_code
	String desc_long
	String reference_no
	String ledger_date
	Integer ledger_qtyin
	Integer ledger_qty_out
	Integer adjustment
	BigDecimal unitcost
	Integer runningqty
	BigDecimal wcost
	BigDecimal runningbalance
}

@TupleConstructor
class StockCardPrint {
	String source_department
	String dest_department
	String document_desc
	String reference_no
	String ledger_date
	Integer ledger_qtyin
	Integer ledger_qty_out
	Integer adjustment
	BigDecimal unitcost
	BigDecimal totalCost
	Integer runningqty
	BigDecimal wcost
	BigDecimal runningbalance
}

@TupleConstructor
class HeaderDtoPrint {
	String descLong
}

@TupleConstructor
class StockCardTransaction {
	String patient
	String caseNo
	String type
	String billNo
	String createdBy
	String lastModifiedBy
}













