package com.hisd3.hismk2.rest.dto

import com.google.gson.annotations.SerializedName

class StockRequestDto {
	public String request_date = null
	public String request_to = null // department
	public String request_by = null // department
	public String request_user = null //employee
	public String request_type = null
	public String purpose = null
	public String dispense_by = null
	public String claimed_by = null
	UUID stockIssue = null
	UUID id = null
}

class RequestItems {
	@SerializedName("id")
	private String id
	
	@SerializedName("item")
	private def item = ItemRequest
	
	@SerializedName("quantity_requested")
	private Integer quantity_requested
	
	@SerializedName("unit_cost")
	private BigDecimal unit_cost
	
	@SerializedName("preparedQty")
	private Integer preparedQty
	
	@SerializedName("isPosted")
	private Boolean isPosted
	
	@SerializedName("isRejected")
	private Boolean isRejected
	
	@SerializedName("remarks")
	private String remarks
	
	@SerializedName("isNew")
	private Boolean isNew

	@SerializedName("status")
	private String status

	final String getId() {
		return this.id
	}
	
	final def getItem() {
		return this.item
	}
	
	final Integer getQuantityRequested() {
		return this.quantity_requested
	}
	
	final BigDecimal getUnitCost() {
		return this.unit_cost
	}
	
	final Integer getPreparedQty() {
		return this.preparedQty
	}
	
	final Boolean getIsPosted() {
		return this.isPosted
	}
	
	final Boolean getIsRejected() {
		return this.isRejected
	}
	
	final String getRemarks() {
		return this.remarks
	}
	
	final String getIsNew() {
		return this.isNew
	}
	
}

class ItemRequest {
	@SerializedName("id")
	private String id
	@SerializedName("sku")
	private String sku
	@SerializedName("brand")
	private String brand
	@SerializedName("descLong")
	private String descLong
	@SerializedName("unit_of_usage")
	private def unit_of_usage
	
	final String getId() {
		return this.id
	}
	
	final String getSku() {
		return this.sku
	}
	
	final String getBrand() {
		return this.brand
	}
	
	final String getDescLong() {
		return this.descLong
	}
}

class UpdateStockRequestDto {
	String stockRequestId
	Arrays requestItems
}

class UpdateStockRequestItem {
	String stockRequestItemId
	Integer preparedQty
	String status
}