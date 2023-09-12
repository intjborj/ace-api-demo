package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.inventory.Item

class DepartItemDto {
	UUID id
	Item item
	Department department
	boolean allow_trade
	
}
