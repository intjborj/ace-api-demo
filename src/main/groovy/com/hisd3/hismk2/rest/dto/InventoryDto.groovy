package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.inventory.Item


class InventoryDto  {
	Integer onhand
	BigDecimal last_wcost
	Integer monthly_count
}

//inventory list of DTO

class BeginningItemDto {
	String id
	Item item
	Department department
	Integer	qty
	BigDecimal unitCost
	Boolean posted
	Boolean isNew
}

