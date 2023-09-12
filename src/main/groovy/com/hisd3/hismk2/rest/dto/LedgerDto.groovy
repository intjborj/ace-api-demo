package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.Department

import java.time.Instant

class LedgerDto {
	Department sourceDep
	Department destinationDep
	UUID documentTypes
	UUID item
	String referenceNo
	Instant ledgerDate
	Integer ledgerQtyIn
	Integer ledgerQtyOut
	Integer ledgerPhysical
	BigDecimal ledgerUnitCost
	Boolean isInclude
}

class RawLedgerDto {
	String id
	Department source
	Department destination
	String itemId
	String typeId
	String type
	String ledgerNo
	Integer qty
	Integer physical
	BigDecimal unitcost
	String date
}
