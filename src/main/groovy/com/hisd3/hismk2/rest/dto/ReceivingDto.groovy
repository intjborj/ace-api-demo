package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.PurchaseOrderItems

import java.time.Instant

class ReceivingDto {
	public Instant date_delivered = null
	public String poNumber = null
	public String refSINo = null
	public Instant refSI_date = null
	public String receiving_dept = null
	public String supplier = null
	public String payment_terms = null
	public String userId = null
	public String userFullname = null
	public Boolean emergency_purchase = false
	public String remarks = null
	public Boolean donation = false
	public Boolean consignment = false
	public Boolean asset = false
	public BigDecimal grossAmount = 0.00
	public BigDecimal totalDiscount = 0.00
	public BigDecimal netDiscount = 0.00
	public BigDecimal amount = 0.00
	public BigDecimal vatRate = 0.00
	public Boolean vatInclusive = false
	public BigDecimal inputTax = 0.00
	public BigDecimal netAmount = 0.00
	public UUID account = null
	
}

class ItemsReceiving {
	String id
	Item item
	PurchaseOrderItems refPoItem
	Integer receiveQty
	BigDecimal receiveUnitCost
	BigDecimal recInventoryCost
	BigDecimal receiveDiscountCost
	Boolean isFg
	Boolean isDiscount
	Boolean isPartial
	Boolean isCompleted
	Boolean isNew = false
	Boolean isTax = false
	String expirationDate
	String lotNo
	BigDecimal totalAmount
	BigDecimal inputTax
	BigDecimal netAmount
}


