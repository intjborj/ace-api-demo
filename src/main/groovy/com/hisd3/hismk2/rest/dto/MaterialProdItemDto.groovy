package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.inventory.Item

class MaterialProdItemDto {
	String id
	Item item
	Integer qty
	Boolean isNew
	BigDecimal unitCost
	String type
}


