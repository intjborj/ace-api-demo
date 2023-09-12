package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.DepartmentStockRequest

import java.time.Instant

class DepartmentStockRequestDto {

}

class DepartmentStockRequestItemsDto {
	String id
	Item item
	Integer quantity_requested
	BigDecimal unit_cost
	String remarks
	Boolean isNew

}

